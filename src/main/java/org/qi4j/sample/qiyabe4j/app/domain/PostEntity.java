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
package org.qi4j.sample.qiyabe4j.app.domain;


import org.qi4j.api.association.Association;
import org.qi4j.api.common.Optional;
import org.qi4j.api.entity.EntityComposite;
import org.qi4j.api.injection.scope.Structure;
import org.qi4j.api.injection.scope.This;
import org.qi4j.api.mixin.Mixins;
import org.qi4j.api.query.Query;
import org.qi4j.api.query.QueryBuilder;
import org.qi4j.api.query.grammar.OrderBy;
import org.qi4j.api.structure.Module;
import org.qi4j.api.unitofwork.UnitOfWork;
import org.qi4j.sample.qiyabe4j.app.state.PostState;

import static org.qi4j.api.query.QueryExpressions.*;

@Mixins( PostEntity.Mixin.class )
public interface PostEntity
    extends PostState, EntityComposite
{
    
    @Optional
    Association<AccountEntity> owner();

    /**
     * @return Comments made on this PostEntity, in descending chronological order.
     */
    Query<CommentEntity> findComments();
   
    

    abstract class Mixin
        implements PostEntity
    {

        @Structure
        private Module module;
        @This
        private PostEntity post;
        
        

        @Override
        public Query<CommentEntity> findComments()
        {
            UnitOfWork uow = module.currentUnitOfWork();
            CommentEntity comment = templateFor( CommentEntity.class );
            QueryBuilder<CommentEntity> queryBuilder = module.newQueryBuilder( CommentEntity.class ).
                where( eq( comment.post(), post ) );
            return uow.newQuery( queryBuilder ).
                orderBy( comment.postedAt(), OrderBy.Order.DESCENDING );
        }
        
    }

}
