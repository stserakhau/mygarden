package com.i4biz.mygarden.expression;


import java.util.*;

public class ExpressionTreeBuilder {
    public static void main(String[] args) throws Exception {
//        String expr1 = "(aaa eq 5 or bbb contains val) and (ccc startWith 3 or ddd like 44) or eee numberContains [1,2,3] and fff in [4,5,6]";
//        String expr1 = "aaa ge 5 or bbb le 6 and ccc eq 5";
//        String expr1 = "(aaa eq 5 or bbb neq 6) and (ccc in [ 'my cat', 'not my cat', 'in the world' ] or ddd eq '5')";
//        String expr1 = "ccc in [   'my cat',   'my  cat',   'in  the   world'  ]";
//        String expr1 = "docType in ['BTXML', 'CSV'] and (docSubType eq \"INVOICEEXPORT (Invoice)\" or docTypeVersion eq 'JNJ')";
        String expr1 = "inBound.docType in ['BTXML', 'CSV'] and (docSubType eq \"INVOICEEXPORT (Invoice)\" or docTypeVersion eq 'JNJ') or id eq 362 or id in [153,154]";

        ExpressionNode en = parse(expr1);

        System.out.println(en);
    }

    private static final Map<Character, Character> openCloseStringChars = new HashMap<Character, Character>() {{
        put('\'', '\'');
        put('"', '"');
        put('[', ']');
    }};


    public static final Map<String, Integer> operators = new HashMap<String, Integer>() {{
        put("or", 3);
        put("and", 2);
        put("eq", 1);
        put("neq", 1);
        put("contains", 1);
        put("ncontains", 1);
        put("startWith", 1);
        put("like", 1);
        put("numberContains", 1);
        put("in", 1);
        put("nin", 1);
        put("le", 1);
        put("ge", 1);
        put("leq", 1);
        put("geq", 1);
        put("between", 1);
        put("isNull", 1);
        put("isNotNull", 1);
        put("custom", 1);
    }};

    public static ExpressionNode parse(String expression) {
        List<String> splitted = splitByWord(expression);

        List<String> res = inPolishNotation(splitted);
        List<String> finalRes = concatValues(res);
        return buildTree(finalRes);
    }

    private static List<String> concatValues(List<String> res) {
        List<String> result = new ArrayList<>();
        String property = null;
        String value = null;
        for (String item : res) {
            if (operators.containsKey(item)) {
                if (property != null && value != null) {
                    result.add(property);
                    result.add(value);
                    property = null;
                    value = null;
                }
                result.add(item);
            } else if (property == null && value == null) {
                property = item;
            } else if (property != null && !operators.containsKey(item)) {
                value = value == null ? item : value + " " + item;
            }

        }
        return result;
    }


    private static List<String> splitByWord(String expression) {
        List<String> result = splitExpressionByDelimeter(expression);

        List<String> words = new ArrayList<String>() {{
            add("(");
            add(")");
        }};
        words.addAll(operators.keySet());

        for (String word : words) {
            int wordLen = word.length();
            List<String> res = new ArrayList<>();
            for (String line : result) {
                if (openCloseStringChars.containsKey(line.charAt(0)) || words.contains(line)) {
                    res.add(line);
                    continue;
                }
                int wordStart = -1;
                do {
                    if ("()".contains(word)) {
                        wordStart = line.indexOf(word);
                   /* } else if (line.contains(word + ' ')) {
                        wordStart = line.indexOf(word + ' ');*/
                    } else if (line.contains(word + '(')) {
                        wordStart = line.indexOf(word + '(');
                    } else if (line.contains(' ' + word)) {
                        wordStart = line.indexOf(' ' + word) + 1;
                    } else if (line.contains(')' + word)) {
                        wordStart = line.indexOf(')' + word) + 1;
                    }

                    if (wordStart == 0) {
                        res.add(word);
                    } else if (wordStart > 0) {
                        char prevChar = line.charAt(wordStart - 1);
                        if (" )".indexOf(prevChar) == -1 && !")".equals(word)) {
                            wordStart = -1;
                        } else {
                            res.add(line.substring(0, wordStart));
                            res.add(word);
                        }
                    }
                    if (wordStart > -1) {
                        line = line.substring(wordStart + wordLen);
                    }
                } while (wordStart != -1);

                if (line.length() > 0) {
                    res.add(line);
                }
            }
            result = res;
        }

        return result;
    }

    private static List<String> splitExpressionByDelimeter(String expression) {
        List<String> result = new ArrayList<>();
        {
            char[] chars = expression.toCharArray();

            StringBuilder word = new StringBuilder();
            Character closeChar = null;
            boolean prevOpenedWord = false;
            boolean isOpenedWord = false;
            boolean wasClosed = false;
            for (char chr : chars) {
                if (!isOpenedWord && closeChar == null) {
                    closeChar = openCloseStringChars.get(chr);
                }

                isOpenedWord = closeChar != null && chr != closeChar;

                if (prevOpenedWord && isOpenedWord != prevOpenedWord) {
                    wasClosed = true;
                    word.append(chr);
                }

                prevOpenedWord = isOpenedWord;
                if (!wasClosed && (isOpenedWord || chr != ' ')) {
                    word.append(chr);
                } else {
                    if (word.length() > 0) {
                        result.add(word.toString());
                        word = new StringBuilder();
                    }
                    closeChar = null;
                    wasClosed = false;
                }
            }
            if (word.length() > 0) {
                result.add(word.toString());
            }
        }
        return result;
    }

    private static ExpressionNode buildTree(List<String> res) {
        Stack<ExpressionNode> stack = new Stack<>();

        for (String word : res) {
            Integer priority = operators.get(word);
            if (priority == null) {
                ExpressionNode root = new ExpressionNode(word);
                stack.push(root);
            } else {
                ExpressionNode right = stack.pop();
                ExpressionNode left = stack.pop();

                ExpressionNode root = new ExpressionNode(left, right, word);
                stack.push(root);
            }

        }
        ExpressionNode root = stack.pop();

        if (!stack.isEmpty()) {
            throw new RuntimeException("Invalid expression");
        }

        return root;
    }

    private static List<String> inPolishNotation(List<String> expression) {
        List<String> result = new ArrayList<>();
        Stack<String> stack = new Stack<>();
        for (String line : expression) {
            switch (line) {
                case "(":
                    stack.push(line);
                    break;
                case ")":
                    while (!stack.empty() && !stack.peek().equals("(")) {
                        result.add(stack.pop());
                    }
                    stack.pop();
                    break;
                default:
                    Integer linePriority = operators.get(line);
                    Integer lastStackPriority = !stack.empty() ? operators.get(stack.peek()) : null;
                    if (linePriority != null && lastStackPriority != null && lastStackPriority < linePriority) {
                        while (!stack.empty() && !stack.peek().equals("(") && operators.get(stack.peek()) < linePriority) {
                            result.add(stack.pop());
                        }
                    }

                    if (linePriority != null) {
                        stack.push(line);
                    } else {
                        result.add(line);
                    }
                    break;
            }
        }

        while (!stack.empty()) {
            result.add(stack.pop());
        }
        return result;
    }

}
