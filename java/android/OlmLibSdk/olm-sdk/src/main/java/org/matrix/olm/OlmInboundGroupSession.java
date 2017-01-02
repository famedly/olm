/*
 * Copyright 2016 OpenMarket Ltd
 * Copyright 2016 Vector Creations Ltd
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

package org.matrix.olm;


import android.text.TextUtils;
import android.util.Log;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Class used to create an inbound <a href="http://matrix.org/docs/guides/e2e_implementation.html#handling-an-m-room-key-event">Megolm session</a>.<br>
 * Counter part of the outbound group session {@link OlmOutboundGroupSession}, this class decrypts the messages sent by the outbound side.
 *
 * <br><br>Detailed implementation guide is available at <a href="http://matrix.org/docs/guides/e2e_implementation.html">Implementing End-to-End Encryption in Matrix clients</a>.
 */
public class OlmInboundGroupSession extends CommonSerializeUtils implements Serializable {
    private static final long serialVersionUID = -772028491251653253L;
    private static final String LOG_TAG = "OlmInboundGroupSession";

    /** Session Id returned by JNI.<br>
     * This value uniquely identifies the native inbound group session instance.
     */
    private transient long mNativeId;

    /**
     * Wrapper class to be used in {@link #decryptMessage(String, DecryptIndex, StringBuffer)}
     */
    static public class DecryptIndex {
        /** decrypt index **/
        public long mIndex;
    }

    /**
     * Constructor.<br>
     * Create and save a new native session instance ID and start a new inbound group session.
     * The session key parameter is retrieved from an outbound group session
     * See {@link #createNewSession()} and {@link #initInboundGroupSessionWithSessionKey(String)}
     * @param aSessionKey session key
     * @throws OlmException constructor failure
     */
    public OlmInboundGroupSession(String aSessionKey) throws OlmException {
        if(createNewSession()) {
            if( 0 != initInboundGroupSessionWithSessionKey(aSessionKey)) {
                releaseSession();// prevent memory leak before throwing
                throw new OlmException(OlmException.EXCEPTION_CODE_INIT_INBOUND_GROUP_SESSION,OlmException.EXCEPTION_MSG_INIT_INBOUND_GROUP_SESSION);
            }
        } else {
            throw new OlmException(OlmException.EXCEPTION_CODE_CREATE_INBOUND_GROUP_SESSION, OlmException.EXCEPTION_MSG_NEW_INBOUND_GROUP_SESSION);
        }
    }

    /**
     * Release native session and invalid its JAVA reference counter part.<br>
     * Public API for {@link #releaseSessionJni()}.
     */
    public void releaseSession(){
        releaseSessionJni();
        mNativeId = 0;
    }

    /**
     * Destroy the corresponding OLM inbound group session native object.<br>
     * This method must ALWAYS be called when this JAVA instance
     * is destroyed (ie. garbage collected) to prevent memory leak in native side.
     * See {@link #createNewSessionJni()}.
     */
    private native void releaseSessionJni();

    /**
     * Create and save the session native instance ID.<br>
     * To be called before any other API call.
     * @return true if init succeed, false otherwise.
     */
    private boolean createNewSession() {
        mNativeId = createNewSessionJni();
        return (0 != mNativeId);
    }

    /**
     * Create the corresponding OLM inbound group session in native side.<br>
     * Do not forget to call {@link #releaseSession()} when JAVA side is done.
     * @return native session instance identifier (see {@link #mNativeId})
     */
    private native long createNewSessionJni();

    /**
     * Start a new inbound group session.<br>
     * The session key parameter is retrieved from an outbound group session
     * see {@link OlmOutboundGroupSession#sessionKey()}
     * @param aSessionKey session key
     * @return 0 if operation succeed, -1 otherwise
     */
    private int initInboundGroupSessionWithSessionKey(String aSessionKey) {
        int retCode = -1;

        if(TextUtils.isEmpty(aSessionKey)){
            Log.e(LOG_TAG, "## initInboundGroupSessionWithSessionKey(): invalid session key");
        } else {
            retCode = initInboundGroupSessionWithSessionKeyJni(aSessionKey);
        }

        return retCode;
    }
    private native int initInboundGroupSessionWithSessionKeyJni(String aSessionKey);


    /**
     * Retrieve the base64-encoded identifier for this inbound group session.
     * @return the session ID if operation succeed, null otherwise
     */
    public String sessionIdentifier() {
        return sessionIdentifierJni();
    }
    private native String sessionIdentifierJni();


    /**
     * Decrypt the message passed in parameter.<br>
     * In case of error, null is returned and an error message description is provided in aErrorMsg.
     * @param aEncryptedMsg the message to be decrypted
     * @param aDecryptIndex_out decrypted message index
     * @param aErrorMsg error message description
     * @return the decrypted message if operation succeed, null otherwise.
     */
    public String decryptMessage(String aEncryptedMsg, DecryptIndex aDecryptIndex_out, StringBuffer aErrorMsg) {
        String decryptedMessage = null;

        // sanity check
        if(null == aErrorMsg) {
            Log.e(LOG_TAG,"## decryptMessage(): invalid parameter - aErrorMsg=null");
        } else {
            aErrorMsg.setLength(0);
            decryptedMessage = decryptMessageJni(aEncryptedMsg, aDecryptIndex_out, aErrorMsg, OlmManager.ENABLE_STRING_UTF8_SPECIFIC_CONVERSION);
        }
        return decryptedMessage;
    }
    private native String decryptMessageJni(String aEncryptedMsg, DecryptIndex aDecryptIndex_out, StringBuffer aErrorMsg, boolean aIsUtf8ConversionRequired);


    /**
     * Kick off the serialization mechanism.
     * @param aOutStream output stream for serializing
     * @throws IOException exception
     */
    private void writeObject(ObjectOutputStream aOutStream) throws IOException {
        serializeObject(aOutStream);
    }

    /**
     * Kick off the deserialization mechanism.
     * @param aInStream input stream
     * @throws IOException exception
     * @throws ClassNotFoundException exception
     */
    private void readObject(ObjectInputStream aInStream) throws IOException, ClassNotFoundException {
        deserializeObject(aInStream);
    }

    @Override
    protected boolean createNewObjectFromSerialization() {
        return createNewSession();
    }

    @Override
    protected void releaseObjectFromSerialization() {
        releaseSession();
    }

    /**
     * Return the current inbound group session as a base64 serialized string.<br>
     * The session is serialized and encrypted with aKey.
     * In case of failure, an error human readable
     * description is provide in aErrorMsg.
     * @param aKey encryption key
     * @param aErrorMsg error message description
     * @return pickled base64 string if operation succeed, null otherwise
     */
    @Override
    protected String serializeDataWithKey(String aKey, StringBuffer aErrorMsg) {
        String pickleRetValue = null;

        // sanity check
        if(null == aErrorMsg) {
            Log.e(LOG_TAG,"## serializeDataWithKey(): invalid parameter - aErrorMsg=null");
        } else if(TextUtils.isEmpty(aKey)) {
            aErrorMsg.append("Invalid input parameters in serializeDataWithKey()");
        } else {
            aErrorMsg.setLength(0);
            pickleRetValue = serializeDataWithKeyJni(aKey, aErrorMsg);
        }

        return pickleRetValue;
    }
    /**
     * JNI counter part of {@link #serializeDataWithKey(String, StringBuffer)}.
     * @param aKey encryption key
     * @param aErrorMsg error message description
     * @return pickled base64 string if operation succeed, null otherwise
     */
    private native String serializeDataWithKeyJni(String aKey, StringBuffer aErrorMsg);


    /**
     * Load an inbound group session from a pickled base64 string.<br>
     * See {@link #serializeDataWithKey(String, StringBuffer)}
     * @param aSerializedData pickled inbound group session in a base64 string format
     * @param aKey encrypting key used in {@link #serializeDataWithKey(String, StringBuffer)}
     * @param aErrorMsg error message description
     * @return true if operation succeed, false otherwise
     */
    @Override
    protected boolean initWithSerializedData(String aSerializedData, String aKey, StringBuffer aErrorMsg) {
        boolean retCode = false;
        String jniError;

        if(null == aErrorMsg) {
            Log.e(LOG_TAG, "## initWithSerializedData(): invalid input error parameter");
        } else {
            aErrorMsg.setLength(0);

            if (TextUtils.isEmpty(aSerializedData) || TextUtils.isEmpty(aKey)) {
                Log.e(LOG_TAG, "## initWithSerializedData(): invalid input parameters");
            } else if (null == (jniError = initWithSerializedDataJni(aSerializedData, aKey))) {
                retCode = true;
            } else {
                aErrorMsg.append(jniError);
            }
        }

        return retCode;
    }
    /**
     * JNI counter part of {@link #initWithSerializedData(String, String, StringBuffer)}.
     * @param aSerializedData pickled session in a base64 string format
     * @param aKey key used to encrypted in {@link #serializeDataWithKey(String, StringBuffer)}
     * @return null if operation succeed, an error message if operation failed
     */
    private native String initWithSerializedDataJni(String aSerializedData, String aKey);

    /**
     * Return true the object resources have been released.<br>
     * @return true the object resources have been released
     */
    public boolean isReleased() {
        return (0 == mNativeId);
    }
}
