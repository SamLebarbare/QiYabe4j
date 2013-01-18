package org.qi4j.sample.qiyabe4j.app.domain;

import org.qi4j.api.injection.scope.Service;
import org.qi4j.api.injection.scope.Structure;
import org.qi4j.api.mixin.Mixins;
import org.qi4j.api.service.ServiceActivation;
import org.qi4j.api.service.ServiceComposite;
import org.qi4j.api.structure.Module;
import org.qi4j.api.unitofwork.UnitOfWork;
import org.qi4j.api.usecase.UsecaseBuilder;

@Mixins( DomainBootstrapService.Mixin.class )
public interface DomainBootstrapService
    extends ServiceComposite, ServiceActivation
{

    class Mixin
        implements ServiceActivation
    {

        @Structure
        private Module module;
        @Service
        private DomainEntitiesRepository repository;
        @Service
        private DomainEntitiesFactory factory;

        @Override
        public void activateService()
            throws Exception
        {
            UnitOfWork uow = module.newUnitOfWork( UsecaseBuilder.newUsecase( "Create admin account and initial post" ) );
            if( repository.posts().count() == 0 )
            {
                AccountEntity adminAccount = factory.newAccountEntity( "admin", "admin",
                                                                       "admin@changeme.now",
                                                                       "Admin account" );
                factory.newPostEntity( "Yet Another Blog Engine with Qi4j",
                                       "This blog is deployed to heroku and is using a Redis EntityStore and an ElasticSearch Index/Query service.",
                                       adminAccount );
            }
            uow.complete();
        }

        @Override
        public void passivateService()
            throws Exception
        {
        }
    }

}
