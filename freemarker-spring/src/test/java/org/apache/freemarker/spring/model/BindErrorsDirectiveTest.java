/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.freemarker.spring.model;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.apache.freemarker.spring.example.mvc.users.User;
import org.apache.freemarker.spring.example.mvc.users.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration("classpath:META-INF/web-resources")
@ContextConfiguration(locations = { "classpath:org/apache/freemarker/spring/example/mvc/users/users-mvc-context.xml" })
public class BindErrorsDirectiveTest {

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MessageSource messageSource;

    private MockMvc mockMvc;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }

    @Test
    public void testBasicUsages() throws Exception {
        final User user = new User();
        user.setFirstName("Paul");
        user.setLastName("Temple");
        // set invalid email intentionally to test BindErrorsDirective...
        user.setEmail("");

        mockMvc.perform(post("/users/").param("viewName", "test/model/binderrors-directive-basic-usages")
                .param("firstName", user.getFirstName()).param("lastName", user.getLastName())
                .param("email", user.getEmail()).accept(MediaType.parseMediaType("text/html")))
                .andExpect(status().isOk()).andExpect(content().contentTypeCompatibleWith("text/html")).andDo(print())
                .andExpect(xpath("//div[@class='error']").string(equalToIgnoringWhiteSpace(
                        messageSource.getMessage("user.error.invalid.email", new Object[] { user.getEmail() }, null))))
                .andExpect(xpath("//input[@name='firstName']/@value").string(user.getFirstName()))
                .andExpect(xpath("//input[@name='lastName']/@value").string(user.getLastName()));
    }

}
