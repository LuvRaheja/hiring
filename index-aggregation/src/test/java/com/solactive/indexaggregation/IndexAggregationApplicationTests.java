package com.solactive.indexaggregation;

import com.solactive.indexaggregation.controller.TickController;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class IndexAggregationApplicationTests {

	@Autowired
	TickController controller;
	@Test
	public void contextLoads() {
		Assert.assertTrue(controller!=null);
	}

}
