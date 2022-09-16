/*
 * Creato il 6-ott-04
 * Author: vuoto
 * 
 */
package it.eng.sil.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import it.eng.sil.security.Password;

public class EncryptPwd {

	public static void main(String[] args) {

		if (args.length != 1) {
			System.out.println("USAGE:   java it.eng.sil.util.EncryptPwd OracleJdbcUrl\n");
			System.out.println(
					"Example: java it.eng.sil.util.EncryptPwd jdbc:oracle:thin:silmo/silmo@srvhmd:1521:orahmb");
			System.exit(1);

		}

		List cdnutArray = new ArrayList();
		List passwordArray = new ArrayList();

		Connection con = null;

		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			// String url = "jdbc:oracle:thin:silmo/silmo@srvhmd:1521:orahmb";
			String url = args[0];

			con = DriverManager.getConnection(url);
			Statement cmd = con.createStatement();

			String qry = "SELECT * FROM ts_utente order by strlogin";

			PreparedStatement prepareUpdatePwdHash = con
					.prepareStatement("UPDATE ts_utente SET strpassword= ? where cdnut=?");

			ResultSet rs = cmd.executeQuery(qry);
			while (rs.next()) {

				int cdnut = rs.getInt("cdnut");
				String login = rs.getString("strlogin");
				String password = rs.getString("strpassword");

				if (!password.startsWith("#SHA#")) {
					cdnutArray.add(new Integer(cdnut));
					passwordArray.add(password);

					System.out.println("login=" + login);

				}

			}
			rs.close();

			con.setAutoCommit(false);

			for (int i = 0; i < cdnutArray.size(); i++) {

				String passwordHash = new Password((String) passwordArray.get(i)).getEncValue();
				System.out.println("passwordHash=" + passwordHash);

				prepareUpdatePwdHash.setString(1, passwordHash);
				prepareUpdatePwdHash.setInt(2, ((Integer) cdnutArray.get(i)).intValue());

				prepareUpdatePwdHash.execute();

			}
			con.commit();
			System.out.println("\nAggiornate " + cdnutArray.size() + " password");

			prepareUpdatePwdHash.close();

			cmd.close();
		} catch (Exception e) {

			e.printStackTrace();
			if (con != null) {
				try {
					con.rollback();
				} catch (SQLException e1) {

					e1.printStackTrace();
				}
			}

		} finally {
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e1) {

					e1.printStackTrace();
				}

			}

		}
	}
}
