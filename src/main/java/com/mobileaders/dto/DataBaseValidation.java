package com.mobileaders.dto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import com.mobileaders.dto.DButil;
import com.mobileaders.model.Feed;
import com.mobileaders.model.FeedMessage;
import com.mobileaders.model.Sources;
import com.mobileaders.rssread.RSSFeedParser;

public class DataBaseValidation {
	static Statement st;
	private static ArrayList<Sources> sourcesList;
	static Logger log = Logger.getLogger(DataBaseValidation.class.getName());

	public static ArrayList<Sources> listOfSources() {
		st = DButil.getStatement();
		sourcesList = new ArrayList<>();
		String sql = "SELECT `sourceUrl`,`parenttag`  FROM `Sources`";
		try {
			ResultSet rs = st.executeQuery(sql);
			while (rs.next()) {
				Sources sources=new Sources();
				sources.setSourceUrl(rs.getString(1));
				sources.setParentTag(rs.getString(2));
				sourcesList.add(sources);
				log.info("RSS FEED URL --> " + rs.getString(2));
			}

		} catch (SQLException ex) {
			log.error("Error", ex);
		}

		return sourcesList;
	}

	public static int checkDataInserted(String url) {
		int x = 0;
		RSSFeedParser parser = new RSSFeedParser(url);
		Feed feed = parser.readFeed();
 

		String sql = "SELECT `news_title`  FROM `Newspaper` where `news_title` = '" + feed.getTitle() + "'";

		try {
			st = DButil.getStatement();

			ResultSet rs = st.executeQuery(sql);

			if (rs.next()) {

				x = 1;
			}

 		} catch (SQLException ex) {
			ex.printStackTrace();
			log.error("Error", ex);
		}

		return x;
	}

//check data 

	public static void saveNewsPaper(Sources s) {

		String sQL = "INSERT  INTO `Newspaper`(`date_posted`,`news_title`,`news_conclosion`,`news_link` ) VALUES (  ? ,?, ?,?)";

		PreparedStatement ps = null;
		Connection dbConn = null;

		try {
			dbConn = DButil.getConn();

			ps = dbConn.prepareStatement(sQL);
			dbConn.setAutoCommit(false);
			RSSFeedParser parser = new RSSFeedParser(s.getSourceUrl());
			Feed feed = parser.readFeed();
 
			ps.setString(1, feed.getPubDate());
			ps.setString(2, feed.getTitle());
			ps.setString(3, feed.getDescription());
			ps.setString(4, feed.getLink());

			if (ps != null) {
				int arr = ps.executeUpdate();
				log.info("Number Of inserted Rows:" + arr);
				dbConn.commit();
				
			}
			saveTages(s.getParentTag());
			saveRelationalTages(s.getSourceUrl(), s.getParentTag());
		} catch (SQLException e) {
			e.printStackTrace();
			log.error("Error", e);
		} finally {
			clearResources(dbConn, ps);
		}


	}
	public static void saveTages(String tag) {

		String sQL = "INSERT  INTO `Tags`(`tagName`,`parnet_tag_id`,`flag` ) VALUES (?, ? ,? )";

		PreparedStatement ps = null;
		Connection dbConn = null;

		try {
			dbConn = DButil.getConn();

			ps = dbConn.prepareStatement(sQL);
			dbConn.setAutoCommit(false);
			 
 
			ps.setString(1, tag);
			ps.setString(2, null);
			ps.setString(3,"C");
 
			if (ps != null) {
				int arr = ps.executeUpdate();
				log.info("Number Of inserted Rows:" + arr);
				dbConn.commit();
			}

		} catch (SQLException e) {
			e.printStackTrace();
			log.error("Error", e);
		} finally {
			clearResources(dbConn, ps);
		}

	}
	public static void saveRelationalTages(String url ,String tag) {

		String sQL = "INSERT  INTO `Newspapertotags`(`paperID`,`tagID`  ) VALUES (?, ?  )";

		PreparedStatement ps = null;
		Connection dbConn = null;

		try {
			dbConn = DButil.getConn();

			ps = dbConn.prepareStatement(sQL);
			dbConn.setAutoCommit(false);
			 
 
			ps.setInt(1, getNewsID(url));
			ps.setInt(2, getTagID(tag));
 
			if (ps != null) {
				int arr = ps.executeUpdate();
				log.info("Number Of inserted Rows:" + arr);
				dbConn.commit();
			}

		} catch (SQLException e) {
			e.printStackTrace();
			log.error("Error", e);
		} finally {
			clearResources(dbConn, ps);
		}

	}
	

	private static int getTagID(String tag) {
		int x = 0;
	 

		String sql = "SELECT `tag_Id` FROM `Tags` where `tagName` = '" + tag + "'";

		try {
			st = DButil.getStatement();

			ResultSet rs = st.executeQuery(sql);

			if (rs.next()) {

				x = rs.getInt(1);
			}

			System.out.println(x);
		} catch (SQLException ex) {
			ex.printStackTrace();
			log.error("Error", ex);
		}

		return x;
	}

	public static int getNewsID(String url) {
		int x = 0;
		RSSFeedParser parser = new RSSFeedParser(url);
		Feed feed = parser.readFeed();

		String sql = "SELECT `news_id` FROM `Newspaper` where `news_title` = '" + feed.getTitle() + "'";

		try {
			st = DButil.getStatement();

			ResultSet rs = st.executeQuery(sql);

			if (rs.next()) {

				x = rs.getInt(1);
			}

			System.out.println(x);
		} catch (SQLException ex) {
			ex.printStackTrace();
			log.error("Error", ex);
		}

		return x;
	}

	public static void saveArtical(String url) {

		String sQL = "INSERT  INTO `Article`(`title`,`conclusion`,`media`,`link`,`author`,`guid`,`pubdate`,`newspaper_id`) VALUES (?, ? ,?, ?,?,?,?,?)";

		PreparedStatement ps = null;
		Connection dbConn = null;

		try {
			dbConn = DButil.getConn();

			ps = dbConn.prepareStatement(sQL);
			dbConn.setAutoCommit(false);
			RSSFeedParser parser = new RSSFeedParser(url);
			Feed feed = parser.readFeed();
			int newsPaperID= getNewsID(url);
			for (FeedMessage message : feed.getMessages()) {
				System.out.println("news Id --> " +newsPaperID);

				ps.setString(1, message.getTitle());
				ps.setString(2, message.getDescription());

				ps.setString(3, message.getLink());
				ps.setString(4, message.getLink());
				ps.setString(5, message.getAuthor());
				ps.setString(6, message.getGuid());
				ps.setString(7, message.getPubDate());
				ps.setInt(8, newsPaperID);

				ps.addBatch();

			}
			if (ps != null) {
				int[] arr = ps.executeBatch();
				log.info("Number Of inserted Rows:" + arr.length);
				dbConn.commit();
			}

		} catch (SQLException e) {
			e.printStackTrace();
			log.error("Error", e);
		} finally {
			clearResources(dbConn, ps);
		}

	}

	private static void clearResources(Connection dbConn, Statement ps) {
		try {

			if (null != ps) {
				ps.close();
			}
			if (null != dbConn) {
				dbConn.commit();
				dbConn.close();
			}
		} catch (SQLException e) {
			log.error("failed to clear DB Resources", e);
		}
	}

	public static void main(String[] args) {
System.out.println(checkDataInserted("https://www.vogella.com/article.rss"));

	}
}
