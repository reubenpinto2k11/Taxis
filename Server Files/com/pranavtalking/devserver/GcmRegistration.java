package com.pranavtalking.devserver;

import java.io.IOException;

import com.google.appengine.api.datastore.*;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.Result;
import com.google.android.gcm.server.Sender;

public class GcmRegistration extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private static final String GOOGLE_SERVER_KEY = "AIzaSyDef5-US07A95ckj030mwMboJqVwWCQH74";
	static final String MESSAGE_KEY = "TestMessage";

	public GcmRegistration() {
		super();
	}

	@SuppressWarnings("deprecation")
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		Result result = null;

		
			DatastoreService datastore = DatastoreServiceFactory
					.getDatastoreService();
			
			

			// GCM RedgId of Android device to send push notification
			String regId = "";

			String share = request.getParameter("shareRegId");
			if (share != null && !share.isEmpty()) {
				regId = request.getParameter("regId");
				

				Entity loc = new Entity("GcmID", request.getParameter("regId")); // kind,
																					// key
				loc.setProperty("id", regId);
				datastore.put(loc);

				request.setAttribute("pushStatus", "GCM RegId Received.");
				request.getRequestDispatcher("GcmTest.jsp")

				.forward(request, response);
			} else {
				try {
					
					Query q = new Query("GcmID");
					PreparedQuery pq = datastore.prepare(q);

					List<String> regID = new ArrayList<String>();

					if (pq.countEntities() != 0) {
						for (Entity datastoreResult : pq.asIterable()) {
							regID.add(datastoreResult.getProperty("id")
									.toString());
						}
					}

					String userMessage = request.getParameter("message");
					Sender sender = new Sender(GOOGLE_SERVER_KEY);
					Message message = new Message.Builder().timeToLive(30)
							.delayWhileIdle(true)
							.addData(MESSAGE_KEY, userMessage).build();
					System.out.println("regId: " + regId);
					result = sender.send(message, regID.get(0), 1);
					request.setAttribute("pushStatus", result.toString());
				} catch (IOException ioe) {
					ioe.printStackTrace();
					request.setAttribute("pushStatus",
							"RegId required: " + ioe.toString());
				}

				catch (Exception e) {
					e.printStackTrace();
					request.setAttribute("pushStatus", e.toString());
				}
				request.getRequestDispatcher("GcmTest.jsp").forward(request,
						response);
			}

		}



	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		try {
			doPost(request, response);
		} catch (ServletException e) {
			e.printStackTrace();
			request.setAttribute("pushStatus", e.toString());
		}
	}

}
