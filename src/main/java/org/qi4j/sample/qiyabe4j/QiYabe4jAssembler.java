/*
 * Copyright (c) 2013, Paul Merlin. All Rights Reserved.
 * Copyright (c) 2013, Samuel Loup. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package org.qi4j.sample.qiyabe4j;

import org.qi4j.api.structure.Application;
import org.qi4j.bootstrap.ApplicationAssembler;
import org.qi4j.bootstrap.ApplicationAssembly;
import org.qi4j.bootstrap.ApplicationAssemblyFactory;
import org.qi4j.bootstrap.AssemblyException;
import org.qi4j.bootstrap.LayerAssembly;
import org.qi4j.bootstrap.ModuleAssembly;
import org.qi4j.entitystore.memory.MemoryEntityStoreService;
import org.qi4j.index.elasticsearch.assembly.ESMemoryIndexQueryAssembler;
import org.qi4j.library.conversion.values.EntityToValueService;
import org.qi4j.library.fileconfig.FileConfigurationService;
import org.qi4j.library.http.JettyConfiguration;
import org.qi4j.library.http.JettyServiceAssembler;
import org.qi4j.library.http.WelcomeServletService;
import org.qi4j.sample.qiyabe4j.app.domain.AccountEntity;
import org.qi4j.sample.qiyabe4j.app.domain.CommentEntity;
import org.qi4j.sample.qiyabe4j.app.domain.DomainBootstrapService;
import org.qi4j.sample.qiyabe4j.app.domain.DomainEntitiesFactory;
import org.qi4j.sample.qiyabe4j.app.domain.DomainEntitiesRepository;
import org.qi4j.sample.qiyabe4j.app.domain.PostEntity;
import org.qi4j.sample.qiyabe4j.pres.http.HomeJsonServletService;
import org.qi4j.sample.qiyabe4j.pres.http.PostByIdentityJsonServletService;
import org.qi4j.sample.qiyabe4j.pres.values.AccountValue;
import org.qi4j.sample.qiyabe4j.pres.values.CommentValue;
import org.qi4j.sample.qiyabe4j.pres.values.PostValue;
import org.qi4j.spi.uuid.UuidIdentityGeneratorService;

import static org.qi4j.api.common.Visibility.*;
import static org.qi4j.library.http.Servlets.*;

public class QiYabe4jAssembler
    implements ApplicationAssembler
{

    private final int port;

    public QiYabe4jAssembler( int port )
    {
        this.port = port;
    }

    @Override
    public ApplicationAssembly assemble( ApplicationAssemblyFactory aaf )
        throws AssemblyException
    {
        ApplicationAssembly appAss = aaf.newApplicationAssembly();
        appAss.setName( "QiYabe4j" );
        appAss.setVersion( "1" );
        appAss.setMode( Application.Mode.production );

        // Layers
        LayerAssembly configLayer = appAss.layer( "config" );
        LayerAssembly infraLayer = appAss.layer( "infra" );
        LayerAssembly appLayer = appAss.layer( "application" );
        LayerAssembly presLayer = appAss.layer( "presentation" );

        // Assembly
        ModuleAssembly configModule = assembleConfigLayer( configLayer );
        assembleInfraLayer( infraLayer, configModule );
        assembleApplicationLayer( appLayer );
        assemblePresentationLayer( presLayer, configModule );

        // Layer Uses
        presLayer.uses( appLayer, infraLayer, configLayer );
        appLayer.uses( infraLayer, configLayer );
        infraLayer.uses( configLayer );

        return appAss;
    }

    private ModuleAssembly assembleConfigLayer( LayerAssembly configLayer )
        throws AssemblyException
    {
        ModuleAssembly configModule = configLayer.module( "config" );
        {
            configModule.services( MemoryEntityStoreService.class );
            configModule.services( FileConfigurationService.class ).visibleIn( application );
            return configModule;
        }
    }

    private void assembleInfraLayer( LayerAssembly infraLayer, ModuleAssembly configModule )
        throws AssemblyException
    {
        ModuleAssembly persistenceModule = infraLayer.module( "persistence" );
        {
            persistenceModule.services( UuidIdentityGeneratorService.class, MemoryEntityStoreService.class ).
                visibleIn( application );
            new ESMemoryIndexQueryAssembler().
                withVisibility( application ).
                withConfigModule( configModule ).
                withConfigVisibility( application ).
                assemble( persistenceModule );
        }
    }

    private void assembleApplicationLayer( LayerAssembly appLayer )
        throws AssemblyException
    {
        ModuleAssembly domainModule = appLayer.module( "domain" );
        {
            domainModule.entities( AccountEntity.class, PostEntity.class, CommentEntity.class ).
                visibleIn( application );
            domainModule.services( DomainEntitiesFactory.class, DomainEntitiesRepository.class ).
                visibleIn( application );
            domainModule.services( DomainBootstrapService.class ).instantiateOnStartup();
        }
    }

    private void assemblePresentationLayer( LayerAssembly presLayer, ModuleAssembly configModule )
        throws AssemblyException
    {
        ModuleAssembly valuesModule = presLayer.module( "values" );
        {
            valuesModule.services( EntityToValueService.class ).visibleIn( layer );
            valuesModule.values( AccountValue.class, PostValue.class, CommentValue.class ).visibleIn( layer );
        }
        ModuleAssembly httpModule = presLayer.module( "http" );
        {
            new JettyServiceAssembler().
                withConfigModule( configModule ).
                withConfigVisibility( application ).
                assemble( httpModule );
            JettyConfiguration config = configModule.forMixin( JettyConfiguration.class ).declareDefaults();
            config.hostName().set( "0.0.0.0" );
            config.port().set( port );

            addServlets( serve( "/" ).with( WelcomeServletService.class ) ).to( httpModule );
            addServlets( serve( "/api/home" ).with( HomeJsonServletService.class ) ).to( httpModule );
            addServlets( serve( "/api/post" ).with( PostByIdentityJsonServletService.class ) ).to( httpModule );
        }
    }
}
