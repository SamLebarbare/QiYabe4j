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
import org.qi4j.bootstrap.Energy4Java;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main
{

    private static final Logger LOG = LoggerFactory.getLogger( "org.qi4j.sample.qiyabe4j" );
    private static final int DEFAULT_HTTP_PORT = 5000;
    private static Application application;

    public static void main( String[] args )
        throws Exception
    {
        int port = resolveHttpPort();
        LOG.info( "Starting QiYabe4j! on http://0.0.0.0:{}/", port );
        Energy4Java qi4j = new Energy4Java();
        application = qi4j.newApplication( new QiYabe4jAssembler( port ) );
        installShutdownHook();
        application.activate();
    }

    private static int resolveHttpPort()
    {
        if( System.getenv( "PORT" ) != null )
        {
            return Integer.valueOf( System.getenv( "PORT" ) );
        }
        else
        {
            return DEFAULT_HTTP_PORT;
        }
    }

    private static void installShutdownHook()
    {
        Runtime.getRuntime().addShutdownHook( new Thread( new Runnable()
        {
            public void run()
            {
                if( application != null )
                {
                    try
                    {
                        application.passivate();
                    }
                    catch( Exception ex )
                    {
                        LOG.error( "Unable to passivate application: {}", ex.getMessage(), ex );
                    }
                }
            }
        } ) );
    }
}
