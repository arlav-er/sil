<%@ page  import="it.eng.sil.util.*, java.util.ResourceBundle" %>

<% 
	ResourceBundle resource = ResourceBundle.getBundle("git_autogen");
  	String gitBranch = resource.getString("git.branch");
  	String gitBuildHost = resource.getString("git.build.host");
  	String gitBuildTime = resource.getString("git.build.time");  	
  	String gitBuildUserEmail = resource.getString("git.build.user.email");
  	String gitBuildUserName= resource.getString("git.build.user.name");
  	String gitBuildVersion = resource.getString("git.build.version");
  	String gitClosestTagCommitCount = resource.getString("git.closest.tag.commit.count");
  	String gitClosestTagName = resource.getString("git.closest.tag.name");
  	String gitCommitId = resource.getString("git.commit.id");
  	String gitCommitIdAbbrev = resource.getString("git.commit.id.abbrev");
  	String gitCommitIdDescribe = resource.getString("git.commit.id.describe");
  	String gitCommitIdDescribeShort = resource.getString("git.commit.id.describe-short");
  	String gitCommitMessageFull = resource.getString("git.commit.message.full");
  	String gitCommitMessageShort = resource.getString("git.commit.message.short");
  	String gitCommitTime = resource.getString("git.commit.time");
  	String gitCommitUserEmail = resource.getString("git.commit.user.email");
  	String gitCommitUserName = resource.getString("git.commit.user.name");
  	String gitDirty = resource.getString("git.dirty");
  	String gitRemoteOriginUrl = resource.getString("git.remote.origin.url");
  	String gitTags = resource.getString("git.tags");
 %>