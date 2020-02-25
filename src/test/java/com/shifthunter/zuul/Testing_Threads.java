package com.shifthunter.zuul;

import org.junit.Ignore;
import org.junit.Test;

import com.shifthunter.threads.grp_1.Sender;
import com.shifthunter.threads.grp_2.ThreadA;

public class Testing_Threads {

//	@Ignore
	@Test
	public void test_Thread_Grp_1() {
		new ThreadA().run();

	}

//	@Ignore
	@Test
	public void test_Thread_Grp_2() {
		new Sender().send("Helloo");;

	}

}
