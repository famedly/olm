package org.matrix.olm;

import android.support.test.runner.AndroidJUnit4;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import java.util.Iterator;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class OlmUtilityTest {
    private static final String LOG_TAG = "OlmAccountTest";
    private static final int GENERATION_ONE_TIME_KEYS_NUMBER = 50;

    private static OlmManager mOlmManager;

    @BeforeClass
    public static void setUpClass(){
        // load native lib
        mOlmManager = new OlmManager();

        String version = mOlmManager.getOlmLibVersion();
        assertNotNull(version);
        Log.d(LOG_TAG, "## setUpClass(): lib version="+version);
    }

    /**
     * Test the signing API
     */
    @Test
    public void test01VerifyEd25519Signing() {
        String fingerPrintKey = null;
        String errorMsg = new String();
        String message = "{\"key1\":\"value1\",\"key2\":\"value2\"};";

        // create account
        OlmAccount account = new OlmAccount();
        assertNotNull(account);

        // sign message
        String messageSignature = account.signMessage(message);
        assertNotNull(messageSignature);

        // get identity key
        JSONObject identityKeysJson = account.identityKeys();
        assertNotNull(identityKeysJson);
        try {
            fingerPrintKey = identityKeysJson.getString(OlmAccount.JSON_KEY_FINGER_PRINT_KEY);
            assertTrue("fingerprint key missing",!TextUtils.isEmpty(fingerPrintKey));
        } catch (JSONException e) {
            e.printStackTrace();
            assertTrue("Exception MSg="+e.getMessage(), false);
        }

        // instance utility
        OlmUtility utility = new OlmUtility();
        boolean isVerified = utility.verifyEd25519Signature(messageSignature, fingerPrintKey, message, errorMsg);
        assertTrue(isVerified);

        utility.releaseUtility();
    }


    @Test
    public void test02sha256() {
        OlmUtility utility = new OlmUtility();
        String msgToHash = "The quick brown fox jumps over the lazy dog";

        String hashResult = utility.sha256(msgToHash);
        assertFalse(TextUtils.isEmpty(hashResult));

        utility.releaseUtility();
    }
}
