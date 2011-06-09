/* Copyright 2008 Aaron Porter
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.stripesstuff.stripersist;

/**
 * <p>
 * At startup Stripersist will search for classes implementing StripersistInit
 * using the standard Stripes auto-discovery method. Any classes found
 * implementing StripersistInit are instantiated and the init() method is called
 * on them.
 * </p>
 * 
 * @author Aaron Porter
 * 
 */
public interface StripersistInit {
    public void init();
}
