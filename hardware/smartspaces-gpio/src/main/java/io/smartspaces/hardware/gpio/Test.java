/*
 * Copyright (C) 2016 Keith M. Hughes
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package io.smartspaces.hardware.gpio;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.smartspaces.hardware.services.NfcScanner;
import io.smartspaces.hardware.services.Pn532NfcScanner;

/**
 * Test driver for the PN532.
 * 
 * @author Keith M. Hughes
 */
public class Test {

	  public static void main(String[] args) {
		  NfcScanner nfc = new Pn532NfcScanner();
		  
		  nfc.startup();
		  
		  nfc.getObservable().subscribe(new Observer<String>() {

			@Override
			public void onSubscribe(Disposable d) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onNext(String value) {
				System.out.println("UUID is " + value);
			}

			@Override
			public void onError(Throwable e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onComplete() {
				// TODO Auto-generated method stub
				
			} });
	}

}
