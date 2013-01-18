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
package org.qi4j.sample.qiyabe4j.app.state;

import org.qi4j.api.common.Optional;
import org.qi4j.api.property.Property;
import org.qi4j.library.constraints.annotation.Email;
import org.qi4j.library.constraints.annotation.MinLength;

public interface AccountState
{

    @MinLength( 4 )
    Property<String> login();

    @MinLength( 4 )
    Property<String> password();

    @Email
    Property<String> email();

    @Optional
    Property<String> displayName();
}
