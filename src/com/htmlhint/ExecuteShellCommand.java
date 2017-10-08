package com.htmlhint;

import com.intellij.openapi.diagnostic.Logger;
import org.apache.commons.lang.StringUtils;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExecuteShellCommand {
  private static final Logger LOG = Logger.getInstance(ExecuteShellCommand.class);

  // NOTE: for Debug
  // public static void main(String[] args) {
  //   Result result = run("./", "./test_data/index.html", "htmlhint", "");
  // }

  private static final Pattern pattern = Pattern.compile("([\\w\\W]+):(\\d+):(\\d+):\\s+([\\w\\W]+\\.)\\s+\\[(\\w+)\\/([\\w\\W]+)\\]");

  public Result executeCommand(String cwd, String command) {
    StringBuilder output = new StringBuilder();
    StringBuilder errOutput = new StringBuilder();

    LOG.debug("cwd: " + cwd + " command: " + command);

    Result result = new Result();

    Process p;
    try {
      p = Runtime.getRuntime().exec(command, new String[]{}, new File(cwd));
      p.waitFor();
      BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));

      BufferedReader errReader = new BufferedReader(new InputStreamReader(p.getErrorStream()));
      String errLine;
      while ((errLine = errReader.readLine()) != null) errOutput.append(errLine).append('\n');

      String file = null;
      String line;
      while ((line = reader.readLine()) != null) {
        boolean found = false;
        Matcher matcher = pattern.matcher(line);
        if (file == null) file = line;

        if (matcher.find() && matcher.groupCount() == 6) {
          Warn warn = new Warn();
          warn.filepath = matcher.group(1);
          warn.line = Integer.parseInt(matcher.group(2));
          warn.column = Integer.parseInt(matcher.group(3));
          warn.message = matcher.group(4);
          warn.level = matcher.group(5);
          warn.rule = matcher.group(6);
          result.warns.add(warn);
        }

        while (matcher.find()) {
          String debug_string = String.format(
              "I found the text" +
              " \"%s\" starting at " +
              "index %d and ending at index %d.%n",
              matcher.group(),
              matcher.start(),
              matcher.end()
              );
          LOG.debug(debug_string);
          found = true;
        }
        output.append(line).append('\n');
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

    result.errorOutput = errOutput.toString();
    LOG.debug("output: " + output.toString());
    LOG.debug("error: " + errOutput.toString());

    return result;
  }

  public static Result run(String cwd, String path, String htmlhintBin, String htmlhintrc) {
    ExecuteShellCommand obj = new ExecuteShellCommand();
    String command = htmlhintBin;
    command += " -f unix ";
    if (StringUtils.isNotEmpty(htmlhintrc)) command += " -c " + htmlhintrc;
    command += " " + path;
    return obj.executeCommand(cwd, command);
  }

  public static class Result {
    public List<Warn> warns = new ArrayList<Warn>();
    public String errorOutput;

    @Override
    public String toString() {
      String result = "[";
      for (Warn warn: warns) result += warn.toString() + "\n";
      result += "]";
      return result;
    }
  }

  public static class Warn {
    public String filepath;
    public int line;
    public int column;
    public String message;
    public String level;
    public String rule;

    @Override
    public String toString() {
      return "<Warn:" +
        " @filepath=" + filepath +
        " @line=" + String.valueOf(line) +
        " @column=" + String.valueOf(column) +
        " @message=" + message +
        " @level=" + level +
        " @rule=" + rule +
        ">";
    }
  }
}
