/*
 * Copyright 2004-2005 The Apache Software Foundation or its licensors,
 *                     as applicable.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.jackrabbit.rmi.client;

import java.rmi.RemoteException;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.InvalidQueryException;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;

import org.apache.jackrabbit.rmi.remote.RemoteQuery;
import org.apache.jackrabbit.rmi.remote.RemoteQueryManager;

/**
 * Local adapter for the JCR-RMI {@link RemoteQueryManager RemoteQueryManager}
 * inteface. This class makes a remote query manager locally available using
 * the JCR {@link QueryManager QueryManager} interface.
 *
 * @author Philipp Koch
 * @see javax.jcr.query.QueryManager QueryManager
 * @see org.apache.jackrabbit.rmi.remote.RemoteQueryManager
 */
public class ClientQueryManager extends ClientObject implements QueryManager {

    /** The current session */
    private Session session;

    /** The adapted remote query manager. */
    private RemoteQueryManager remote;

    /**
     * Creates a client adapter for the given remote query manager.
     *
     * @param session current session
     * @param remote remote query manager
     * @param factory adapter factory
     */
    public ClientQueryManager(Session session, RemoteQueryManager remote,
            LocalAdapterFactory factory) {
        super(factory);
        this.session = session;
        this.remote = remote;
    }

    /** {@inheritDoc} */
    public Query createQuery(String statement, String language)
            throws InvalidQueryException, RepositoryException {
        try {
            RemoteQuery query = remote.createQuery(statement, language);
            return factory.getQuery(session, query);
        } catch (RemoteException ex) {
            throw new RemoteRepositoryException(ex);
        }
    }

    /** {@inheritDoc} */
    public Query getQuery(Node node)
            throws InvalidQueryException, RepositoryException {
        try {
            // TODO fix this remote node dereferencing hack
            RemoteQuery query = remote.getQuery(node.getPath());
            return factory.getQuery(session, query);
        } catch (RemoteException ex) {
            throw new RemoteRepositoryException(ex);
        }
    }

    /** {@inheritDoc} */
    public String[] getSupportedQueryLanguages() {
        try {
            return remote.getSupportedQueryLanguages();
        } catch (RemoteException ex) {
            throw new RemoteRuntimeException(ex);
        }
    }

}
