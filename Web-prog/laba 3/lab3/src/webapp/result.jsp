<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="models.ResultRecord" %>
<% ResultRecord result = (ResultRecord) request.getAttribute("result"); %>
<!doctype html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Результат проверки</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/style/result.css?v=soft-result">
</head>
<body class="<%= result == null ? "" : result.getIsHit() ? "hit" : "miss" %>">
<main>
<h1>Результат проверки</h1>
<% if (result != null) { %>
<div class="table-container">
<table>
    <thead>
    <tr>
        <th>X</th>
        <th>Y</th>
        <th>R</th>
        <th>Результат</th>
        <th>Время</th>
        <th>Время выполнения, мс</th>
    </tr>
    </thead>
    <tbody>
    <tr>
        <td title="<%= result.getX() %>"><%= String.format(java.util.Locale.ROOT, "%.4f", result.getX()) %></td>
        <td title="<%= result.getY() %>"><%= String.format(java.util.Locale.ROOT, "%.4f", result.getY()) %></td>
        <td><%= result.getR() %></td>
        <td class="result-status"><%= result.getIsHit() ? "Попадание" : "Промах" %></td>
        <td><%= result.getCurrentTime() %></td>
        <td title="<%= result.getExecutionTime() %>"><%= String.format(java.util.Locale.ROOT, "%.3f", result.getExecutionTime()) %></td>
    </tr>
    </tbody>
</table>
</div>
<% } else { %>
<p>Результат проверки отсутствует.</p>
<% } %>
<p><a href="<%= request.getContextPath() %>/controller<%= result != null ? "?selectedR=" + (int) result.getR() : "" %>">Проверить другую точку</a></p>
</main>
</body>
</html>
