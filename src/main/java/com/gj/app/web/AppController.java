package com.gj.app.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.gj.app.context.AppContext;

@RestController
@RequestMapping("/brain")
@CrossOrigin
public class AppController {
	@Autowired
	AppContext context;
	@RequestMapping(value = "/netMode/{mode}", method = RequestMethod.POST)
	@ResponseBody
	public void incById(@PathVariable("mode") int mode) {
		System.out.println(mode);
		context.setNetMode(mode);
	}
}
