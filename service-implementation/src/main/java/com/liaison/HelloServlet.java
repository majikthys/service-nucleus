package com.liaison;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.liaison.commons.jpa.DAOInit;
import com.liaison.commons.jpa.DAOUtil;
import com.liaison.commons.util.datasource.OracleDataSource;
import com.liaison.hellodao.Main;

public class HelloServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		PrintWriter out = response.getWriter();

		Main m = new Main();
		try {

			out.println(m.doit("Hello Cruel Cruel World"));
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

}