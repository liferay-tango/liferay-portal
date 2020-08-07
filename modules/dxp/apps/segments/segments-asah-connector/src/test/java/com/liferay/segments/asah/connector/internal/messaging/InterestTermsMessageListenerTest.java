/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of the Liferay Enterprise
 * Subscription License ("License"). You may not use this file except in
 * compliance with the License. You can obtain a copy of the License by
 * contacting Liferay, Inc. See the License for the specific language governing
 * permissions and limitations under the License, including but not limited to
 * distribution rights of the Software.
 *
 *
 *
 */

package com.liferay.segments.asah.connector.internal.messaging;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * @author David Arques
 */
@RunWith(MockitoJUnitRunner.class)
public class InterestTermsMessageListenerTest {

	@Before
	public void setUp() {
		ReflectionTestUtil.setFieldValue(
			_interestTermsMessageListener, "_interestTermsChecker",
			_interestTermsChecker);
	}

	@Test
	public void testDoReceive() throws Exception {
		Message message = new Message();

		message.put("companyId", RandomTestUtil.randomLong());
		message.put("userId", RandomTestUtil.randomString());

		_interestTermsMessageListener.doReceive(message);

		Mockito.verify(
			_interestTermsChecker, Mockito.times(1)
		).checkInterestTerms(
			message.getLong("companyId"), message.getString("userId")
		);
	}

	@Test
	public void testDoReceiveWithEmptyAcClientUserId() throws Exception {
		Message message = new Message();

		message.put("userId", StringPool.BLANK);

		_interestTermsMessageListener.doReceive(message);

		Mockito.verify(
			_interestTermsChecker, Mockito.never()
		).checkInterestTerms(
			Mockito.anyLong(), Mockito.anyString()
		);
	}

	@Test
	public void testDoReceiveWithNullAcClientUserId() throws Exception {
		_interestTermsMessageListener.doReceive(new Message());

		Mockito.verify(
			_interestTermsChecker, Mockito.never()
		).checkInterestTerms(
			Mockito.anyLong(), Mockito.anyString()
		);
	}

	@Mock
	private InterestTermsChecker _interestTermsChecker;

	private final InterestTermsMessageListener _interestTermsMessageListener =
		new InterestTermsMessageListener();

}