package com.KoreaIT.example.JAM;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import com.KoreaIT.example.JAM.util.DBUtil;
import com.KoreaIT.example.JAM.util.SecSql;
import com.KoreaIT.example.JAM.util.util;

public class App {
	public void start() {
		System.out.println("=프로그램 시작==");
		Scanner sc = new Scanner(System.in);

		while (true) {
			System.out.print("명령어 ) ");
			String cmd = sc.nextLine().trim();

			Connection conn = null;

			try {
				Class.forName("com.mysql.jdbc.Driver");
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}

			String url = "jdbc:mysql://127.0.0.1:3306/JAM?useUnicode=true&characterEncoding=utf8&autoReconnect=true&serverTimezone=Asia/Seoul&useOldAliasMetadataBehavior=true&zeroDateTimeNehavior=convertToNull";

			try {

				conn = DriverManager.getConnection(url, "root", "");

				int actionResult = doAction(conn, sc, cmd);

				if (actionResult == -1) {
					System.out.println("프로그램을 종료합니다");
					break;
				}

			} catch (SQLException e) {
				System.out.println("@@@@@@에러 1@@@@@@:" + e);
			} finally {
				try {
					if (conn != null && !conn.isClosed()) {
						conn.close();
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private int doAction(Connection conn, Scanner sc, String cmd) {

		if (cmd.equals("exit")) {
			System.out.println("프로그램을 종료합니다");
			return -1;
		}

		if (cmd.equals("member join")) {
			String loginId = null;
			String loginPw = null;
			String loginPwConfirm = null;
			String name = null;

			System.out.println("==회원 가입==");

			while (true) {
				System.out.printf("아이디 : ");
				loginId = sc.nextLine().trim();

				if (loginId.length() == 0) {
					System.out.println("아이디를 입력해주세요");
					continue;
				}

				SecSql sql = new SecSql();

				sql.append("SELECT COUNT(*) > 0");
				sql.append("FROM `member`");
				sql.append("WHERE loginId = ?", loginId);

				boolean isLoginIdDup = DBUtil.selectRowBooleanValue(conn, sql);

				if (isLoginIdDup) {
					System.out.println(loginId + "는(은) 이미 사용중인 아이디입니다");
					continue;
				}

				break;
			}

			while (true) {
				System.out.printf("비밀번호 : ");
				loginPw = sc.nextLine().trim();

				if (loginPw.length() == 0) {
					System.out.println("비밀번호를 입력해주세요");
					continue;
				}

				boolean loginPwCheck = true;

				while (true) {
					System.out.printf("비밀번호 확인 : ");
					loginPwConfirm = sc.nextLine().trim();

					if (loginPwConfirm.length() == 0) {
						System.out.println("비밀번호 확인을 입력해주세요");
						continue;
					}

					if (loginPw.equals(loginPwConfirm) == false) {
						System.out.println("비밀번호가 일치하지 않습니다. 다시 입력해주세요");
						loginPwCheck = false;
						break;
					}
					break;
				}
				if (loginPwCheck) {
					break;
				}
			}

			while (true) {
				System.out.printf("이름 : ");
				name = sc.nextLine();

				if (name.length() == 0) {
					System.out.println("이름을 입력해주세요");
					continue;
				}
				break;
			}

			SecSql sql = new SecSql();

			sql.append("INSERT INTO `member`");
			sql.append("SET regDate = NOW()");
			sql.append(", updateDate = NOW()");
			sql.append(", loginId = ?", loginId);
			sql.append(", loginPw = ?", loginPw);
			sql.append(", `name` = ?", name);

			int id = DBUtil.insert(conn, sql);

			System.out.println(id + "번 회원님, 가입 되었습니다");

		} else if (cmd.equals("article write")) {
			System.out.println("==게시물 작성==");
			System.out.printf("제목 : ");
			String title = sc.nextLine();
			System.out.printf("내용 : ");
			String body = sc.nextLine();

			SecSql sql = new SecSql();

			sql.append("INSERT INTO article");
			sql.append("SET regDate = NOW()");
			sql.append(", updateDate = NOW()");
			sql.append(", title = ?", title);
			sql.append(", `body` = ?", body);

			int id = DBUtil.insert(conn, sql);

			System.out.println(id + "번 글이 생성 되었습니다");

		} else if (cmd.startsWith("article detail ")) {
			int id = Integer.parseInt(cmd.split(" ")[2]);

			System.out.println("==게시물 상세보기==");

			SecSql sql = new SecSql();
			sql.append("SELECT *");
			sql.append("FROM article");
			sql.append("WHERE id = ?", id);

			Map<String, Object> articleMap = DBUtil.selectRow(conn, sql);

			if (articleMap.isEmpty()) {
				System.out.println(id + "번 글은 존재하지 않습니다");
				return 0;
			}

			Article article = new Article(articleMap);

			System.out.println("번호 : " + article.id);
			System.out.println("작성날짜 : " + util.getNowDateTimeStr(article.regDate));
			System.out.println("수정날짜 : " + util.getNowDateTimeStr(article.updateDate));
			System.out.println("제목 : " + article.title);
			System.out.println("내용 : " + article.body);

		} else if (cmd.startsWith("article delete ")) {
			int id = Integer.parseInt(cmd.split(" ")[2]);

			System.out.println("==게시물 삭제==");

			SecSql sql = new SecSql();
			sql.append("SELECT COUNT(*)");
			sql.append("FROM article");
			sql.append("WHERE id = ?", id);

			int articlesCount = DBUtil.selectRowIntValue(conn, sql);

			if (articlesCount == 0) {
				System.out.println(id + "번 글은 존재하지 않습니다");
				return 0;
			}

			sql = new SecSql();
			sql.append("DELETE FROM article");
			sql.append("WHERE id = ?", id);

			DBUtil.delete(conn, sql);

			System.out.println(id + "번 글이 삭제 되었습니다");

		} else if (cmd.startsWith("article modify ")) {
			int id = Integer.parseInt(cmd.split(" ")[2]);

			System.out.println("==게시물 수정==");
			System.out.printf("새 제목 : ");
			String title = sc.nextLine();
			System.out.printf("새 내용 : ");
			String body = sc.nextLine();

			SecSql sql = new SecSql();

			sql.append("UPDATE article");
			sql.append("SET updateDate = NOW()");
			sql.append(", title = ?", title);
			sql.append(", `body` = ?", body);
			sql.append("WHERE id = ?", id);

			DBUtil.update(conn, sql);

			System.out.println(id + "번 글이 수정 되었습니다");

		} else if (cmd.equals("article list")) {
			System.out.println("==게시물 목록==");

			List<Article> articles = new ArrayList<>();

			SecSql sql = new SecSql();

			sql.append("SELECT *");
			sql.append("FROM article");
			sql.append("ORDER BY id DESC;");

			List<Map<String, Object>> articleListMap = DBUtil.selectRows(conn, sql);

			for (Map<String, Object> articleMap : articleListMap) {
				articles.add(new Article(articleMap));
			}

			if (articles.size() == 0) {
				System.out.println("게시글이 없습니다");
				return 0;
			}

			System.out.println("번호   /   제목");

			for (Article article : articles) {
				System.out.printf("%4d   /   %s\n", article.id, article.title);
			}
		}

		return 0;
	}

}
