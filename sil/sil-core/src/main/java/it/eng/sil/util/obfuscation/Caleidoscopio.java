/*
 * Created on 26-lug-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package it.eng.sil.util.obfuscation;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class Caleidoscopio {

	private static final int COMMIT_AT = 1000;

	public static void main(String[] args) {

		if (args.length != 1) {
			System.out.println("USAGE: java " + Caleidoscopio.class.getName() + " OracleJdbcUrl\n");
			System.out.println("Example: java " + Caleidoscopio.class.getName()
					+ " jdbc:oracle:thin:modenadev/dev@concordia:1521:orac10");
			System.exit(1);
		}

		Connection con = null;
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			String url = args[0];

			System.out.println("Database: " + url);

			con = DriverManager.getConnection(url);

			con.setAutoCommit(false);

			aggiornaAnLavoratore(con);

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

	private static void aggiornaAnLavoratore(Connection con) throws SQLException, ParseException {
		Random r = new Random(new java.util.Date().getTime());

		System.out.println(
				"\nAggiornamento tabella AN_LAVORATORE (mescolamento cognomi e nomi, generazione data di nascita causale e ricreazione codice fiscale)\n\n");
		PreparedStatement pstmPfPrincipal = con.prepareStatement(
				"UPDATE AN_LAVORATORE set strcognome=?, strnome=?, strcodicefiscale=?, datnasc=?, NUMKLOLAVORATORE=NUMKLOLAVORATORE+1 where cdnlavoratore=? ");

		String qry = "SELECT cdnlavoratore, strnome, strcognome, codcomnas , STRSESSO   from an_lavoratore ORDER BY DBMS_RANDOM.VALUE";

		Statement cmd = con.createStatement();
		ResultSet rs = cmd.executeQuery(qry);

		int counter = 0;
		while (rs.next()) {
			int cdn_lavoratore_1 = rs.getInt("cdnlavoratore");
			String cognome_1 = rs.getString("strcognome");
			String nome_1 = rs.getString("strnome");
			String cod_com_nascita_1 = rs.getString("codcomnas");
			String sesso_1 = rs.getString("STRSESSO");

			String cognome_2 = "ROSSI";
			String nome_2 = "MARIO";
			int cdn_lavoratore_2 = 0;
			String sesso_2 = "M";
			String cod_com_nascita_2 = "A944";

			if (rs.next()) {
				cdn_lavoratore_2 = rs.getInt("cdnlavoratore");
				cognome_2 = rs.getString("strcognome");
				nome_2 = rs.getString("strnome");
				cod_com_nascita_2 = rs.getString("codcomnas");
				sesso_2 = rs.getString("STRSESSO");
			}

			aggiornaNomeCognomeCfDatNasc(r, pstmPfPrincipal, cdn_lavoratore_1, cognome_2, nome_1, sesso_1,
					cod_com_nascita_1);
			counter++;
			if (cdn_lavoratore_2 != 0) {
				aggiornaNomeCognomeCfDatNasc(r, pstmPfPrincipal, cdn_lavoratore_2, cognome_1, nome_2, sesso_2,
						cod_com_nascita_2);
				counter++;
			}

			if (counter % COMMIT_AT == 0) {
				con.commit();
				stampaMsg(counter);
			}

		}
		rs.close();
		cmd.close();
		pstmPfPrincipal.close();
		con.commit();
		stampaMsg(counter);

	}

	private static void stampaMsg(int counter) {
		String msg = String.format("Mescolate %7d righe", counter);
		System.out.println(msg);
	}

	private static void aggiornaNomeCognomeCfDatNasc(Random r, PreparedStatement prepareUpdatePwdHash,
			int cdnLavoratore, String cognome, String nome, String sesso, String cod_com_nascita)
			throws SQLException, ParseException {

		prepareUpdatePwdHash.setString(1, cognome);
		prepareUpdatePwdHash.setString(2, nome);

		boolean isDuplicato;
		do {
			isDuplicato = false;
			try {
				String[] dataNascita = generaDataCasuale(r);

				String cf = CodiceFiscale.CalcolaCodiceFiscale(cognome, nome, sesso, cod_com_nascita, dataNascita[0],
						dataNascita[1], dataNascita[2]);

				SimpleDateFormat df = new SimpleDateFormat("yyyy-mm-dd");
				Date ds = df.parse(dataNascita[2] + "-" + dataNascita[1] + "-" + dataNascita[2]);

				prepareUpdatePwdHash.setString(3, cf);
				prepareUpdatePwdHash.setDate(4, new java.sql.Date(ds.getTime()));

				prepareUpdatePwdHash.setInt(5, cdnLavoratore);

				prepareUpdatePwdHash.execute();
			} catch (SQLException ex) {
				if (ex.getMessage().indexOf("STRCODICEFISCALE") > 0) {
					isDuplicato = true;

				} else {
					ex.printStackTrace();
					System.exit(2);
				}
			}
		} while (isDuplicato);

	}

	private static String[] generaDataCasuale(Random rnd) {

		String dd, mm, yy;
		int dateRnd = Math.abs((rnd.nextInt()));
		int d = (dateRnd % 28) + 1;
		if (d < 10)
			dd = String.valueOf("0" + d);
		else
			dd = String.valueOf(d);

		int m = (dateRnd % 12) + 1;
		if (m < 10)
			mm = String.valueOf("0" + m);
		else
			mm = String.valueOf(m);

		int y = (dateRnd % 65) + 24;
		if (y < 10)
			yy = String.valueOf("0" + y);
		else
			yy = String.valueOf(y);

		return new String[] { dd, mm, yy };

	}

}
