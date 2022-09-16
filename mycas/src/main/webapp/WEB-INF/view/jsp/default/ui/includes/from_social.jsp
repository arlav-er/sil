

			<%
				request.setAttribute("lxt_username", request.getParameter("lxt_username"));
							request.setAttribute("lxt_password", request.getParameter("lxt_password"));
							request.setAttribute("lxt_target", request.getParameter("lxt_target"));
							request.setAttribute("lxt_loginTicket", request.getParameter("lxt_loginTicket"));
							request.setAttribute("lxt_flowExecutionKey", request.getParameter("lxt_flowExecutionKey"));
			%>


			<script>
				function addLoadEvent(func) {
					var oldonload = window.onload;
					if (typeof window.onload != 'function') {
						window.onload = func;
					} else {
						window.onload = function() {
							oldonload();
							func();
						}
					}
				}

				function doSubmit() {
					document.getElementById("fm1").submit();
				}
				addLoadEvent(doSubmit);
			</script>

			<form id="fm1" method="post">
				<input type="hidden" name="username" value="${lxt_username}"> <input type="hidden" name="password"
					value="${lxt_password}" /> <input type="hidden" name="TARGET" value="${lxt_target}" /> <input type="hidden"
					name="lt" value="${lxt_loginTicket}" /> <input type="hidden" name="execution" value="${lxt_flowExecutionKey}" />
				<input type="hidden" name="_eventId" value="submit" />
			</form>
