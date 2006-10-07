/****************************************************************
 * Licensed to the Apache Software Foundation (ASF) under one   *
 * or more contributor license agreements.  See the NOTICE file *
 * distributed with this work for additional information        *
 * regarding copyright ownership.  The ASF licenses this file   *
 * to you under the Apache License, Version 2.0 (the            *
 * "License"); you may not use this file except in compliance   *
 * with the License.  You may obtain a copy of the License at   *
 *                                                              *
 *   http://www.apache.org/licenses/LICENSE-2.0                 *
 *                                                              *
 * Unless required by applicable law or agreed to in writing,   *
 * software distributed under the License is distributed on an  *
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY       *
 * KIND, either express or implied.  See the License for the    *
 * specific language governing permissions and limitations      *
 * under the License.                                           *
 ****************************************************************/

package org.apache.james.jspf.policies;

import org.apache.james.jspf.core.SPF1Record;
import org.apache.james.jspf.exceptions.NeutralException;
import org.apache.james.jspf.exceptions.NoneException;
import org.apache.james.jspf.exceptions.PermErrorException;
import org.apache.james.jspf.exceptions.TempErrorException;

public abstract class AbstractNestedPolicy implements NestedPolicy, Policy {

    private Policy childPolicy;

    public void setChildPolicy(Policy children) {
        if (children == null) {
            throw new IllegalStateException("This policy needs a valid child policy: "+this.getClass().toString());
        }
        this.childPolicy = children;
    }
    
    public SPF1Record getSPFRecord(String currentDomain) throws PermErrorException, TempErrorException, NoneException, NeutralException {
        SPF1Record res = getSPFRecordOverride(currentDomain);
        if (res == null && childPolicy != null) {
            res = childPolicy.getSPFRecord(currentDomain);
            if (res == null) {
                res = getSPFRecordFallback(currentDomain);
            } else {
                res = getSPFRecordPostFilter(currentDomain, res);
            }
        }
        return res;
    }

    protected SPF1Record getSPFRecordPostFilter(String currentDomain, SPF1Record res) throws PermErrorException, TempErrorException, NoneException, NeutralException {
        return res;
    }

    protected SPF1Record getSPFRecordFallback(String currentDomain) throws PermErrorException, TempErrorException, NoneException, NeutralException {
        return null;
    }

    protected SPF1Record getSPFRecordOverride(String currentDomain) throws PermErrorException, TempErrorException, NoneException, NeutralException {
        return null;
    }
    
}