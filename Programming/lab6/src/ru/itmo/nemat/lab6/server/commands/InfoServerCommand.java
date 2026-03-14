package ru.itmo.nemat.lab6.server.commands;

import ru.itmo.nemat.lab6.common.interaction.Request;
import ru.itmo.nemat.lab6.common.interaction.Response;
import ru.itmo.nemat.lab6.server.managers.CollectionManager;
import java.util.Date;

public class InfoServerCommand extends ServerCommand {
    private final CollectionManager collectionManager;

    public InfoServerCommand(CollectionManager collectionManager) {
        super("info", "вывести информацию о коллекции");
        this.collectionManager = collectionManager;
    }

    @Override
    public Response execute(Request request) {
        if (request.getArgs().length > 0) {
            return new Response("Команда " + getName() + " не принимает аргументов!", false);
        }

        Date lastInitTime = collectionManager.getLastInitTime();
        Date lastSaveTime = collectionManager.getLastSaveTime();

        String lastInitStr = (lastInitTime == null) ? "в этой сессии еще не было" : lastInitTime.toString();
        String lastSaveStr = (lastSaveTime == null) ? "в этой сессии еще не было" : lastSaveTime.toString();

        StringBuilder sb = new StringBuilder();
        sb.append("Сведения о коллекции:\n");
        sb.append("  Тип: ").append(collectionManager.collectionType()).append("\n");
        sb.append("  Количество элементов: ").append(collectionManager.collectionSize()).append("\n");
        sb.append("  Дата последней инициализации: ").append(lastInitStr).append("\n");
        sb.append("  Дата последнего сохранения: ").append(lastSaveStr);

        return new Response(sb.toString(), true);
    }
}