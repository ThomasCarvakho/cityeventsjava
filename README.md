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

## Diagrama
![Diagrama UML](docs/CityEvents_UML_Diagram.png)
