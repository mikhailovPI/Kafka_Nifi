# 1. Развёртывание и настройка Kafka-кластера в Yandex Cloud
## Kafka кластер:
![img_1.png](img_1.png)
![img_2.png](img_2.png)
## Хосты:
![img_3.png](img_3.png)
## Пользователи:
![img_4.png](img_4.png)
## Schema Registry:
![img_5.png](img_5.png)
## Файл схемы: ./test.json
## Скриншот ответа вызова curl http://localhost:8081/subjects
![img_8.png](img_8.png)
## Скриншот ответа вызова curl curl -X GET http://localhost:8081/subjects/<название_схемы>/versions
![img_9.png](img_9.png)
## Топик:
![img_7.png](img_7.png)
## Отправка сообщений в топик
Запустите класс YandexKafkaProducer, в логах будет информация об отправленных сообщенияхю.
## Чтение сообщений из топика
Запустите класс YandexKafkaConsumer, в логах будет информация о прочитанных сообщениях.
## Скриншоты, подтверждающие успешную передачу и чтение сообщений
![img_10.png](img_10.png)
## Вывод команды kafka-topics.sh --describe.
команда:
```
curl.exe -X GET "https://rc1a-b4vq5pcf2hdbp40r.mdb.yandexcloud.net:443/topics/test-topic" `
--user "kafka-test-user:kafka-test-pass" `
--cacert "C:\Users\abaev\.kafka\YandexInternalRootCA.crt" `
-k
```
результат:
![img_11.png](img_11.png)


# 2. Интеграция Kafka с внешними системами Apache NiFi

## Инструкция по конфигурированию приложения

1. Перейдите в веб-интерфейс NiFi по адресу:  
   `http://localhost:8080/nifi`

2. **Настройка процессора GetFile**:
   - Добавьте процессор `GetFile` для чтения CSV-файлов:
      - Перетащите на рабочее пространство кнопку "Processor" из левого верхнего угла
      - Выберите тип `GetFile`
   - Настройте параметры (двойной щелчок по процессору → вкладка Properties):
      - `Input Directory`: `/opt/nifi/nifi-current/data/`
      - `Polling Interval`: `5 sec`
   - Нажмите `Apply` для сохранения настроек

3. **Настройка процессора PublishKafkaRecord_2_0**:
   - Добавьте процессор `PublishKafkaRecord_2_0`
   - Соедините его с `GetFile` (соединение "Success")
   - Настройте параметры:
      - `Kafka Brokers`: `kafka-1:9092`
      - `Topic Name`: `nifi-topic`
      - `Use Transactions`: `false`
      - `Record Reader`:
         1. Выберите "Reference parameter"
         2. Нажмите "Create new service"
         3. Во вкладке "Compatible Controller Services" выберите `CSVReader`
         4. Нажмите "Create"
      - `Record Writer`:
         1. Выберите "Reference parameter"
         2. Нажмите "Create new service"
         3. Во вкладке "Compatible Controller Services" выберите `JsonRecordSetWriter`
         4. Нажмите "Create"

4. **Активация контроллеров**:
   - Перейдите в вкладку с контроллерами (справа от `CSVReader` и `JsonRecordSetWriter`)
   - Переведите оба контроллера в статус `Enabled`:
      1. Нажмите кнопку с молнией справа
      2. Выберите "Enable"
      3. Дождитесь запуска
      4. Нажмите "Close"

5. **Дополнительные настройки PublishKafkaRecord_2_0**:
   - Во вкладке `Relationships` настройте:
      - `failure`: `terminate`
      - `success`: `terminate`
   - Нажмите `Apply` для сохранения настроек

6. **Запуск процессоров**:
   - Убедитесь, что все процессоры подключены и настроены
   - Выделите процессоры
   - Нажмите кнопку `Start` (в верхнем меню или в контекстном меню каждого процессора)

7. Скопируйте в папку nifi_data файл input_example.csv, переименуйте его в input.csv

## Результат
1. В веб-интерфейсе NiFi будет видна переача данных см в файле *./img.png*

# Kafka
1. Запустите NifiKafkaConsumerExample.

## Результат
2. В логах информация о получении данных см. logs