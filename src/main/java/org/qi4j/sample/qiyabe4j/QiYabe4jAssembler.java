package org.qi4j.sample.qiyabe4j;

import org.qi4j.api.common.Visibility;
import org.qi4j.api.structure.Application;
import org.qi4j.bootstrap.ApplicationAssembler;
import org.qi4j.bootstrap.ApplicationAssembly;
import org.qi4j.bootstrap.ApplicationAssemblyFactory;
import org.qi4j.bootstrap.AssemblyException;
import org.qi4j.bootstrap.LayerAssembly;
import org.qi4j.bootstrap.ModuleAssembly;
import org.qi4j.entitystore.memory.MemoryEntityStoreService;
import org.qi4j.library.http.JettyConfiguration;
import org.qi4j.library.http.JettyServiceAssembler;
import org.qi4j.library.http.Servlets;
import org.qi4j.library.http.WelcomeServletService;

public class QiYabe4jAssembler
    implements ApplicationAssembler
{

    private final int port;

    public QiYabe4jAssembler( int port )
    {
        this.port = port;
    }

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

        // Config Modules
        ModuleAssembly configModule = configLayer.module( "config" );
        configModule.services( MemoryEntityStoreService.class );

        // Presentation Modules
        ModuleAssembly httpModule = presLayer.module( "http" );
        new JettyServiceAssembler().
            withConfigModule( configModule ).
            withConfigVisibility( Visibility.application ).
            assemble( httpModule );
        JettyConfiguration config = configModule.forMixin( JettyConfiguration.class ).declareDefaults();
        config.hostName().set( "0.0.0.0" );
        config.port().set( port );

        Servlets.addServlets( Servlets.serve( "/" ).with( WelcomeServletService.class ) ).to( httpModule );

        // Layer Uses
        presLayer.uses( appLayer, configLayer );
        appLayer.uses( infraLayer, configLayer );
        infraLayer.uses( configLayer );

        return appAss;
    }

}
