# City Events – Sistema de Eventos (Java)

Projeto acadêmico da disciplina **Programação de Soluções Computacionais** – Universidade Anhembi Morumbi.  
Sistema em **Java (console)** para cadastro e consulta de eventos, com persistência em arquivo.

---

## Funcionalidades
- Cadastro de categorias (Show, Esporte, Festa).
- Cadastro de eventos (nome, endereço, categoria, data/hora, descrição).
- Listagem de eventos ordenados por data.
- Consultar eventos acontecendo agora e eventos já passados.
- Participar / cancelar participação em eventos.
- Ver meus eventos confirmados.
- Dados salvos em `data/events.data`.

---

## Como executar
1. Clonar o repositório e abrir em sua IDE Java (IntelliJ, VS Code, Eclipse ou NetBeans).
2. Compilar e executar a classe `Main`.
3. Interagir pelo menu no console.

---

## Exemplo de uso
==== MENU PRINCIPAL ====

[0] Cadastrar novo evento

[1] Listar eventos (ordenados por data)

[2] Participar de evento

[3] Cancelar participação

[4] Meus eventos confirmados

[5] O que está acontecendo agora?

[6] Eventos que já passaram

[7] Sair


---

## Diagrama de Classes 

```mermaid
classDiagram
    %% ======== Domínio ========
    class User {
      String id
      String nome
      String endereco
      String email
    }

    class Category {
      String id
      String nome
    }

    class Event {
      String id
      String nome
      String endereco
      String categoryId
      LocalDateTime start
      LocalDateTime end
      String descricao
    }

    class Participation {
      String userId
      String eventId
      ParticipationStatus status
    }

    class ParticipationStatus {
      <<enum>>
      CONFIRMED
      CANCELED
    }

    %% ======== Utilitários / Persistência ========
    class DateUtil {
      +parseIso(String) LocalDateTime
      +formatIso(LocalDateTime) String
      +isNowBetween(LocalDateTime, LocalDateTime) boolean
    }

    class FileRepository {
      +readAll() List~String~
      +writeAll(List~String~) void
    }

    class CategoryRepository {
      +add(Category) void
      +findAll() List~Category~
      +exportSectionLines() List~String~
      +importFromLines(List~String~) void
    }

    class EventRepository {
      +add(Event) void
      +findAll() List~Event~
      +exportSectionLines() List~String~
      +importFromLines(List~String~) void
    }

    class ParticipationRepository {
      +setStatus(String userId, String eventId, ParticipationStatus) void
      +confirmedEventIds(String userId) List~String~
      +exportSectionLines() List~String~
      +importFromLines(List~String~) void
    }

    class Main {
      +menuConsole() void
      +saveAll(...) void
    }

    %% ======== Relações ========
    Event --> Category : belongs to
    Participation --> User
    Participation --> Event
    Participation --> ParticipationStatus

    Event ..> DateUtil : usa datas ISO
    CategoryRepository ..> FileRepository : persiste
    EventRepository ..> FileRepository : persiste
    ParticipationRepository ..> FileRepository : persiste

    Main ..> CategoryRepository
    Main ..> EventRepository
    Main ..> ParticipationRepository

  ```

### Estrutura Simplificada

- **User** → id, nome, endereço, email  
- **Category** → id, nome  
- **Event** → id, nome, endereço, categoryId, start, end, descrição  
  - pertence a **Category**  
- **Participation** → userId, eventId, status  
  - status definido em **ParticipationStatus (CONFIRMED / CANCELED)**  
- **DateUtil** → parseIso, formatIso, isNowBetween  
- **FileRepository** → readAll, writeAll  
- **CategoryRepository / EventRepository / ParticipationRepository**  
  - usam FileRepository para salvar/carregar dados  
- **Main** → controla o menu do sistema (listar, participar, cancelar, etc.)
