package org.qi4j.sample.qiyabe4j.app.domain;

import org.joda.time.DateTime;
import org.qi4j.api.common.Optional;
import org.qi4j.api.entity.EntityBuilder;
import org.qi4j.api.injection.scope.Structure;
import org.qi4j.api.mixin.Mixins;
import org.qi4j.api.structure.Module;

@Mixins( DomainEntitiesFactory.Mixin.class )
public interface DomainEntitiesFactory
{

    AccountEntity newAccountEntity( String login, String password, String email, @Optional String displayName );

    PostEntity newPostEntity( String title, String content, @Optional AccountEntity account );

    CommentEntity newCommentEntity( PostEntity post, String content, @Optional AccountEntity account );

    class Mixin
        implements DomainEntitiesFactory
    {

        @Structure
        private Module module;

        @Override
        public AccountEntity newAccountEntity( String login, String password, String email, String displayName )
        {
            EntityBuilder<AccountEntity> builder = module.currentUnitOfWork().newEntityBuilder( AccountEntity.class );
            AccountEntity account = builder.instance();
            account.login().set( login );
            account.password().set( password );
            account.email().set( email );
            account.displayName().set( displayName );
            return builder.newInstance();
        }

        @Override
        public PostEntity newPostEntity( String title, String content, AccountEntity account )
        {
            EntityBuilder<PostEntity> builder = module.currentUnitOfWork().newEntityBuilder( PostEntity.class );
            PostEntity post = builder.instance();
            post.title().set( title );
            post.content().set( content );
            post.owner().set( account );
            post.postedAt().set( new DateTime() );
            return builder.newInstance();
        }

        @Override
        public CommentEntity newCommentEntity( PostEntity post, String content, AccountEntity account )
        {
            EntityBuilder<CommentEntity> builder = module.currentUnitOfWork().newEntityBuilder( CommentEntity.class );
            CommentEntity comment = builder.instance();
            comment.post().set( post );
            comment.content().set( content );
            comment.owner().set( account );
            comment.postedAt().set( new DateTime() );
            return builder.newInstance();
        }
    }

}
