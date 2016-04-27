/**
 * Copyright 2015 Otto (GmbH & Co KG)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.schedoscope.metascope.controller;

import javax.servlet.http.HttpServletRequest;

import org.schedoscope.metascope.service.UserEntityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ErrorController extends ViewController {

  private static final String TEMPLATE_ERROR = "util";

  @Autowired
  private UserEntityService userEntityService;

  @RequestMapping("/accessdenied")
  public ModelAndView accessdenied(HttpServletRequest request) {
    ModelAndView mav = createView("accessdenied");
    if (userEntityService.isAuthenticated()) {
      mav.addObject("userEntityService", userEntityService);
      mav.addObject("admin", userEntityService.isAdmin());
    }
    return mav;
  }

  @RequestMapping("/notfound")
  public ModelAndView notfound(HttpServletRequest request) {
    ModelAndView mav = createView("notfound");
    if (userEntityService.isAuthenticated()) {
      mav.addObject("userEntityService", userEntityService);
      mav.addObject("admin", userEntityService.isAdmin());
    }
    return mav;
  }

  @Override
  protected String getTemplateUri() {
    return TEMPLATE_ERROR;
  }

}
