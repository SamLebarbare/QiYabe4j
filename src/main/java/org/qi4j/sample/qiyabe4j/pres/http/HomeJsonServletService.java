package org.qi4j.sample.qiyabe4j.pres.http;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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
import org.qi4j.api.query.Query;
import org.qi4j.api.service.ServiceComposite;
import org.qi4j.api.structure.Module;
import org.qi4j.api.type.CollectionType;
import org.qi4j.api.type.ValueType;
import org.qi4j.api.unitofwork.UnitOfWork;
import org.qi4j.api.usecase.UsecaseBuilder;
import org.qi4j.library.conversion.values.EntityToValue;
import org.qi4j.sample.qiyabe4j.app.domain.DomainEntitiesRepository;
import org.qi4j.sample.qiyabe4j.app.domain.PostEntity;
import org.qi4j.sample.qiyabe4j.pres.values.PostValue;

@Mixins( HomeJsonServletService.Mixin.class )
public interface HomeJsonServletService
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
            UnitOfWork uow = module.newUnitOfWork( UsecaseBuilder.newUsecase( "Home JSON" ) );
            try
            {
                // Fetch last 10 posts
                Query<PostEntity> postsEntities = entitiesRepository.posts().maxResults( 10 );

                // Convert entities to values
                List<PostValue> postValues = new ArrayList<PostValue>();
                for( PostEntity postEntity : postsEntities )
                {
                    postValues.add( entityToValue.convert( PostValue.class, postEntity ) );
                }

                // Serialize values to JSON
                resp.setCharacterEncoding( "UTF-8" );
                resp.setContentType( "application/json" );
                new JSONWriterSerializer( resp.getWriter() ).
                    serialize( postValues, new CollectionType( List.class, new ValueType( PostValue.class ) ) );
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
