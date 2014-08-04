/*
 * Copyright (C) 2012 The Android Open Source Project
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

package download;

import com.google.android.vending.expansion.downloader.impl.DownloaderService;

public class DownloaderMainService extends DownloaderService {
	private static final String BASE64_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAjbYO9yzsllDE3XGPQ7+rchEt/0mRzuTEbUM8l5ovdO87padd5KIP9ADy21F6z8BOpgaGkUPBYH0qaaarUWhkCLR25JjBRvKR7PmgiANWaDguGQ9KQB1eHwOn/jPVi9aF511NR29cMGpsHYyyio+iWuSTS5auX7NorNrKO6PlCTAHysbxjV2CVa869cI+KaR32zN7GACEjHXiK3lLTQkoJ/RwrwvtezO4dyLopWXL5u2YUco288AuKrtfI86T8jCBfe9tKk4dOZ5Q8HRRtNkjRn1QBNooYkijqeZx2zsvWQ8FXxNNs8Hzjv/OdBmkxKgSKobzxKA13P1G++5Oxum34wIDAQAB";
	private static final byte[] SALT = new byte[] {
        -20, 47, 30, -128, -123, -60, 74, -64, 51, 88, -55, -45, 37, -57, -36, -13, -31, 32, -2,
        21
    };

    @Override
    public String getPublicKey() {
        return BASE64_PUBLIC_KEY;
    }

    @Override
    public byte[] getSALT() {
        return SALT;
    }

    @Override
    public String getAlarmReceiverClassName() {
        return DownloaderAlarmReceiver.class.getName();
    }

}
