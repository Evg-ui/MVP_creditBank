Приложение "Кредитный банк".
ПОРЯДОК ЗАПУСКА ВСЕХ МС:
0. КАФКА
1. CALCULATOR порт 8080
2. STATEMENT порт 8082
3. DEAL порт 8081
4. DOSSIER порт 8083
5. GATEWAY порт 8000

1. Пользователь отправляет заявку на кредит.
2. МС Заявка осуществляет прескоринг заявки и если прескоринг проходит, то заявка сохраняется в МС Сделка и отправляется
в МС калькулятор.
3. МС Калькулятор возвращает через МС Заявку пользователю 4 предложения (сущность "LoanOffer") по кредиту с разными
условиями (например без страховки, со страховкой, с зарплатным клиентом, со страховкой и зарплатным клиентом) или отказ.
4. Пользователь выбирает одно из предложений, отправляется запрос в МС Заявка, а оттуда в МС Сделка, где заявка
на кредит и сам кредит сохраняются в базу.
5. МС Досье отправляет клиенту письмо с текстом "Ваша заявка предварительно одобрена, завершите оформление".
6. Клиент отправляет запрос в МС Сделка со всеми своими полными данными о работодателе и прописке.
Происходит скоринг данных в МС Калькулятор, МС Калькулятор рассчитывает все данные по кредиту (ПСК, график платежей и тд),
 МС Сделка сохраняет обновленную заявку и сущность кредит сделанную на основе CreditDto полученного из КК со статусом
 CALCULATED в БД.
7. После валидации МС Досье отправляет письмо на почту клиенту с одобрением или отказом.
Если кредит одобрен, то в письме присутствует ссылка на запрос "Сформировать документы"
8. Клиент отправляет запрос на формирование документов в МС Досье, МС Досье отправляет клиенту на почту документы
для подписания и ссылку на запрос на согласие с условиями.
9. Клиент может отказаться от условий или согласиться.
Если согласился - МС Досье на почту отправляет код и ссылку на подписание документов, куда клиент должен отправить
полученный код в МС Сделка.
10. Если полученный код совпадает с отправленным, МС Сделка выдает кредит (меняет статус сущности "Кредит" на ISSUED,
а статус заявки на CREDIT_ISSUED)

***Microservice Calculator Калькулятор***:
API:
POST: /calculator/offers - расчёт возможных условий кредита. Request - LoanStatementRequestDto, response - List<LoanOfferDto>
POST: /calculator/calc - валидация присланных данных + скоринг данных + полный расчет параметров кредита.
Request - ScoringDataDto, response CreditDto.

Логика работы API:
POST: /calculator/offers
По API приходит LoanStatementRequestDto.
На основании LoanStatementRequestDto происходит прескоринг, создаётся 4 кредитных предложения LoanOfferDto
на основании всех возможных комбинаций булевских полей isInsuranceEnabled и isSalaryClient (false-false, false-true, true-false, true-true).

К примеру: в зависимости от страховых услуг увеличивается/уменьшается процентная ставка и сумма кредита, базовая ставка
хардкодится в коде через property файл. Например цена страховки 100к (или прогрессивная, в зависимости
от запрошенной суммы кредита), ее стоимость добавляется в тело кредита, но она уменьшает ставку на 3.
Цена зарплатного клиента 0, уменьшает ставку на 1.
Ответ на API - список из 4х LoanOfferDto от "худшего" к "лучшему" (чем меньше итоговая ставка, тем лучше).

POST: /calculator/calc
По API приходит ScoringDataDto.
Происходит скоринг данных, высчитывание итоговой ставки(rate), полной стоимости кредита(psk),
размер ежемесячного платежа(monthlyPayment), график ежемесячных платежей (List<PaymentScheduleElementDto>).
Логику расчета параметров кредита можно найти в интернете, полученный результат сверять с имеющимися
в интернете калькуляторами графиков платежей и ПСК.
Ответ на API - CreditDto, насыщенный всеми рассчитанными параметрами.

***Microservice Deal Сделка***:
API:
POST: /deal/statement - расчёт возможных условий кредита. Request - LoanStatementRequestDto, response - List<LoanOfferDto>
POST: /deal/offer/select - Выбор одного из предложений. Request LoanOfferDto, response void.
POST: /deal/calculate/{statementId} - завершение регистрации + полный подсчёт кредита.
Request - FinishRegistrationRequestDto, param - String, response void.
POST: /deal/document/{statementId}/send - запрос на отправку документов. Интеграция Kafka.
POST: /deal/document/{statementId}/sign - запрос на подписание документов. Интеграция Kafka.
POST: /deal/document/{statementId}/code - подписание документов. Интеграция Kafka.

Логика работы API:
POST: /deal/statement
По API приходит LoanStatementRequestDto
На основе LoanStatementRequestDto создаётся сущность Client и сохраняется в БД.
Создаётся Statement со связью на только что созданный Client и сохраняется в БД.
Отправляется POST запрос на /calculator/offers МС Калькулятор через RestClient
Каждому элементу из списка List<LoanOfferDto> присваивается id созданной заявки (Statement)
Ответ на API - список из 4х LoanOfferDto от "худшего" к "лучшему".

POST: /deal/offer/select
По API приходит LoanOfferDto
Достаётся из БД заявка(Statement) по statementId из LoanOfferDto.
В заявке обновляется статус, история статусов(List<StatementStatusHistoryDto>), принятое предложение LoanOfferDto
устанавливается в поле appliedOffer.
Заявка сохраняется.

POST: /deal/calculate/{statementId}
По API приходит объект FinishRegistrationRequestDto и параметр statementId (String).
Достаётся из БД заявка(Statement) по statementId.
ScoringDataDto насыщается информацией из FinishRegistrationRequestDto и Client, который хранится в Statement
Отправляется POST запрос на /calculator/calc МС Калькулятор с телом ScoringDataDto через RestClient.
На основе полученного из кредитного конвейера CreditDto создаётся сущность Credit и сохраняется в базу со статусом CALCULATED.
В заявке обновляется статус, история статусов.
Заявка сохраняется.

В рамках Deal реализована интеграция с Kafka. Все настройки Kafka и Zookeeper через докер хранятся в файле docker-compose.yml
Настройки кафки для приложения - properties.
Для запуска можно воспользоваться встроенным терминалом Idea командой docker-compose up -d. Погасить командой docker-compose down.
Проверить состояние кластера - командой docker ps.
Через кафку осуществляется взаимодействие с потенциальным клиентом - осуществляется почтовая рассылка по этапам поданной заявки.
Настройки кафки-консьюмера находятся в приложении Dossier (см.ниже), настройки кафки-продюсера - в папке configs и services.

Прим:

В кафке заведены 6 топиков, соответствующие темам, по которым необходимо направить письмо на почту Клиенту:
finish-registration
create-documents
send-documents
send-ses
credit-issued
statement-denied

В рамках Deal реализована интеграция с БД Postgres. Взаимодействие осуществляется через liquibase. ChangeLogs находятся
в папке приложения resources. Первое сохранение заявки в базу происходит после осуществления прескоринга и выбора клиентом
одного из 4 предложений.

***Microservice Statement Заявка***:
API:
POST: /statement - Прескоринг + запрос на расчёт возможных условий кредита. Request - LoanStatementRequestDto.
Response - List<LoanOfferDto>
POST: /statement/offer - Выбор одного из предложений. Request LoanOfferDto. Response void.

Логика работы API:
POST: /statement
По API приходит LoanStatementRequestDto
На основе LoanStatementRequestDto происходит прескоринг.
Отправляется POST-запрос на /deal/statement в МС deal через RestClient.
Ответ на API - список из 4х LoanOfferDto от "худшего" к "лучшему".

POST: /statement/offer
По API приходит LoanOfferDto
Отправляется POST-запрос на /deal/offer/select в МС deal через RestClient.

***Microservice Dossier Кредитное Досье***:
В обязанности МС-dossier будет входить обработка сообщений из Кафки от МС-deal на каждом шаге,
который требует отправки письма на почту Клиенту.
1. Формирование письма и документов
2. Отправка письма на почту Клиенту

Выступает в качестве консьюмера для Kafka. Все основные настройки Kafka вынесены в sharedConfigs - отдельный модуль.


***Microservice Gateway**:
МС представляет из себя аналог "фронта" для общения клиента с банком, имеет админскую панель для ручных правок или
получения информации по заявкам.
Вызывает следующие API:
POST: /statement  (направляет в statement @RequestMapping("/statement"))
По API приходит  LoanStatementRequestDto
На основе LoanStatementRequestDto происходит прескоринг.

POST: /statementSelect (направляет в statement @PostMapping("/statement/offer"))
По API приходит LoanOfferDto
Клиент направляет выбраннное предложение в банк, предложение пишется в базу, заявка и клиент сохраняются в базу.

POST: /statement/registration/{statementId} (направляет в deal   @PostMapping("/calculate/{statementId}"))
По API приходит заявка и данные клиента, происходит скоринг. Все данные сохраняются в базу.

POST: /document/{statementId} (направляет в deal @PostMapping("/document/{statementId}/send"))
По API приходит заявка.
Запрос на отправку документов клиенту.

POST: /document/{statementId}/sign (направляет в deal @PostMapping("/document/{statementId}/sign"))
По API приходит заявка.
Запрос на подписание документов клиенту.

POST: /document/{statementId}/sign/code (направляет в deal @PostMapping("/document/{statementId}/code"))
По API приходит заявка.
Запрос на валидацию кода подтверждения клиенту.

Админские методы(AdminController):
POST: /admin/statement/{statementId} (направляет в deal @GetMapping("/admin/statement/{statementId}"))
Позволяет найти заявку по указанному statementId

POST: /admin/statement (направляет в @GetMapping("/admin/statement"))
Позволяет получить список всех заявок с подробностями.

POST: /admin/statement/{statementId}/status (направляет в deal @PutMapping("/admin/statement/{statementId}/status"))
Позволяет обновить вручную статус заявки по ее statementID
