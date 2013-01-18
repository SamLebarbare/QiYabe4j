package org.qi4j.sample.qiyabe4j.pres.http;

import java.io.IOException;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONException;
import org.qi4j.api.injection.scope.Service;
import org.qi4j.api.injection.scope.Structure;
import org.qi4j.api.json.JSONWriterSerializer;
import org.qi4j.api.mixin.Mixins;
import org.qi4j.api.service.ServiceComposite;
import org.qi4j.api.structure.Module;
import org.qi4j.api.unitofwork.UnitOfWork;
import org.qi4j.api.usecase.UsecaseBuilder;
import org.qi4j.library.conversion.values.EntityToValue;
import org.qi4j.sample.qiyabe4j.app.domain.DomainEntitiesRepository;
import org.qi4j.sample.qiyabe4j.app.domain.PostEntity;
import org.qi4j.sample.qiyabe4j.pres.values.PostValue;

@Mixins( PostByIdentityJsonServletService.Mixin.class )
public interface PostByIdentityJsonServletService
    extends Servlet, ServiceComposite
{

    class Mixin
        extends HttpServlet
    {

        private static final long serialVersionUID = 1L;
        @Structure
        private Module module;
        @Service
        private DomainEntitiesRepository entitiesRepository;
        @Service
        private EntityToValue entityToValue;

        @Override
        protected void doGet( HttpServletRequest req, HttpServletResponse resp )
            throws ServletException, IOException
        {
            UnitOfWork uow = module.newUnitOfWork( UsecaseBuilder.newUsecase( "PostByIdentiy JSON" ) );
            try
            {
          
                PostEntity postEntity = entitiesRepository.postByIdentity(req.getParameterValues("id")[0]);

                // Convert entity to value
                PostValue postValue =  entityToValue.convert( PostValue.class, postEntity );
                
                // Serialize value to JSON
                resp.setCharacterEncoding( "UTF-8" );
                resp.setContentType( "application/json" );
                new JSONWriterSerializer( resp.getWriter() ).serialize(postValue);
                resp.getWriter().close();
                 
                 
            }
            catch( JSONException ex )
            {
                throw new ServletException( ex.getMessage(), ex );
            }
            finally
            {
                // Read-only UoW
                uow.discard();
            }
        }
    }

}
