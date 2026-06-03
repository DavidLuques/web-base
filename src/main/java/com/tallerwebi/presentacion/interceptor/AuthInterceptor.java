package com.tallerwebi.presentacion.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 *  datos.
 */
public class AuthInterceptor implements HandlerInterceptor {

  @Override
  public boolean preHandle(
    HttpServletRequest request,
    HttpServletResponse response,
    Object handler
  ) throws Exception {
    // Check if user is logged in
    Object rol = request.getSession().getAttribute("ROL");

    if (rol == null) {
      // Not logged in, redirect to login page
      response.sendRedirect(request.getContextPath() + "/login");
      return false;
    }

    // Logged in, allow request to proceed
    return true;
  }
}
