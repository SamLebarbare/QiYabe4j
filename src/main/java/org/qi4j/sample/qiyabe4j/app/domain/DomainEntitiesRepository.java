package org.qi4j.sample.qiyabe4j.app.domain;

import org.qi4j.api.injection.scope.Structure;
import org.qi4j.api.mixin.Mixins;
import org.qi4j.api.query.Query;
import org.qi4j.api.query.QueryBuilder;
import org.qi4j.api.query.grammar.OrderBy.Order;
import org.qi4j.api.structure.Module;

import static org.qi4j.api.query.QueryExpressions.*;

@Mixins( DomainEntitiesRepository.Mixin.class )
public interface DomainEntitiesRepository
{

    Query<PostEntity> posts();

    class Mixin
        implements DomainEntitiesRepository
    {

        @Structure
        private Module module;

        @Override
        public Query<PostEntity> posts()
        {
            QueryBuilder<PostEntity> queryBuilder = module.newQueryBuilder( PostEntity.class );
            return module.currentUnitOfWork().newQuery( queryBuilder ).
                orderBy( templateFor( PostEntity.class ).postedAt(), Order.DESCENDING );
        }
    }

}
