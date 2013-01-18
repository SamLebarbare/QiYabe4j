package org.qi4j.sample.qiyabe4j.pres.values;

import org.qi4j.api.common.Optional;
import org.qi4j.api.property.Property;
import org.qi4j.api.value.ValueComposite;
import org.qi4j.sample.qiyabe4j.app.state.PostState;

public interface PostValue
    extends PostState, ValueComposite
{

    /**
     * @return Identity of the Account that own this Post.
     */
    @Optional
    Property<String> owner();
}
