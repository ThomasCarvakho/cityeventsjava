import java.util.Comparator;
import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        // Usuário "logado" - user fixo para simplificar
        User currentUser = new User("u-001", "Thomas", "Lisboa, PT", "thomas@example.com");

        // --- Repositórios + carregamento do arquivo ---
        FileRepository fileRepo = new FileRepository("data/events.data");
        CategoryRepository catRepo = new CategoryRepository(fileRepo);
        EventRepository eventRepo = new EventRepository(fileRepo);
        ParticipationRepository partRepo = new ParticipationRepository(fileRepo);

        List<String> lines = fileRepo.readAll();
        catRepo.importFromLines(lines);
        eventRepo.importFromLines(lines);
        partRepo.importFromLines(lines);
        ensureBaseCategories(catRepo, fileRepo, eventRepo, partRepo);


        //  Menu 
        Scanner sc = new Scanner(System.in);
        boolean running = true;

        while (running) {
            System.out.println("\n==== MENU PRINCIPAL ====");
            System.out.println("[0] Cadastrar novo evento");
            System.out.println("[1] Listar eventos (ordenados por data)");
            System.out.println("[2] Participar de evento");
            System.out.println("[3] Cancelar participação");
            System.out.println("[4] Meus eventos confirmados");
            System.out.println("[5] O que está acontecendo agora?");
            System.out.println("[6] Eventos que já passaram");
            System.out.println("[7] Sair");
            System.out.print("Escolha uma opção: ");

            String op = sc.nextLine();

            switch (op) {
                case "0":
                    cadastrarEvento(sc, catRepo, eventRepo, fileRepo, partRepo);
                    break;
                case "1":
                    listarEventosOrdenados(eventRepo, catRepo);
                    break;
                case "2":
                    participarDeEvento(sc, eventRepo, partRepo, currentUser, fileRepo, catRepo);
                    break;
                case "3":
                    cancelarParticipacao(sc, eventRepo, partRepo, currentUser, fileRepo, catRepo);
                    break;
                case "4":
                    listarMeusConfirmados(eventRepo, partRepo, currentUser);
                    break;
                case "5":
                    listarEventosAcontecendoAgora(eventRepo);
                    break;
                case "6":
                    listarEventosPassados(eventRepo);
                    break;
                case "7":
                    running = false;
                    // salva tudo antes de sair
                    saveAll(fileRepo, catRepo, eventRepo, partRepo);
                    System.out.println("Saindo...");
                    break;
                default:
                    System.out.println("Opção inválida.");
            }
        }

        sc.close();
    }

    //  AÇÕES DO MENU 

    private static void listarEventosOrdenados(EventRepository eventRepo, CategoryRepository catRepo) {
        List<Event> eventos = eventRepo.findAll();
        if (eventos.isEmpty()) {
            System.out.println("Não há eventos cadastrados ainda.");
            return;
        }
        eventos.sort(Comparator.comparing(Event::getStart));
        System.out.println("\n-- Eventos (por data) --");
        for (Event e : eventos) {
            String catName = catRepo.findAll().stream()
                    .filter(c -> c.getId().equals(e.getCategoryId()))
                    .map(Category::getNome)
                    .findFirst()
                    .orElse("Sem categoria");

            System.out.println(
                    e.getId() + " | " + e.getNome() +
                            " | " + DateUtil.formatIso(e.getStart()) +
                            " → " + DateUtil.formatIso(e.getEnd()) +
                            " | Categoria: " + catName
            );
        }
    }

    private static void listarEventosAcontecendoAgora(EventRepository eventRepo) {
        List<Event> eventos = eventRepo.findAll();
        boolean achou = false;
        System.out.println("\n-- Eventos acontecendo agora --");
        for (Event e : eventos) {
            if (DateUtil.isNowBetween(e.getStart(), e.getEnd())) {
                achou = true;
                System.out.println(
                        e.getId() + " | " + e.getNome() +
                                " | " + DateUtil.formatIso(e.getStart()) +
                                " → " + DateUtil.formatIso(e.getEnd())
                );
            }
        }
        if (!achou) System.out.println("Nenhum evento acontecendo neste momento.");
    }

    private static void listarEventosPassados(EventRepository eventRepo) {
        List<Event> eventos = eventRepo.findAll();
        boolean achou = false;
        System.out.println("\n-- Eventos já passados --");
        for (Event e : eventos) {
            if (e.getEnd().isBefore(java.time.LocalDateTime.now())) {
                achou = true;
                System.out.println(
                        e.getId() + " | " + e.getNome() +
                                " | " + DateUtil.formatIso(e.getStart()) +
                                " → " + DateUtil.formatIso(e.getEnd())
                );
            }
        }
        if (!achou) System.out.println("Nenhum evento passado encontrado.");
    }

    private static void participarDeEvento(Scanner sc, EventRepository eventRepo,
                                           ParticipationRepository partRepo, User currentUser,
                                           FileRepository fileRepo, CategoryRepository catRepo) {
        listarEventosOrdenados(eventRepo, catRepo);
        System.out.print("\nDigite o ID do evento para PARTICIPAR (ou ENTER para voltar): ");
        String id = sc.nextLine().trim();
        if (id.isEmpty()) return;

        // valida existência do evento
        boolean existe = eventRepo.findAll().stream().anyMatch(e -> e.getId().equals(id));
        if (!existe) {
            System.out.println("ID não encontrado.");
            return;
        }

        partRepo.setStatus(currentUser.getId(), id, ParticipationStatus.CONFIRMED);
        System.out.println("Participação confirmada!");

        saveAll(fileRepo, catRepo, eventRepo, partRepo);
    }

    private static void cancelarParticipacao(Scanner sc, EventRepository eventRepo,
                                             ParticipationRepository partRepo, User currentUser,
                                             FileRepository fileRepo, CategoryRepository catRepo) {
        listarEventosOrdenados(eventRepo, catRepo);
        System.out.print("\nDigite o ID do evento para CANCELAR (ou ENTER para voltar): ");
        String id = sc.nextLine().trim();
        if (id.isEmpty()) return;

        boolean existe = eventRepo.findAll().stream().anyMatch(e -> e.getId().equals(id));
        if (!existe) {
            System.out.println("ID não encontrado.");
            return;
        }

        partRepo.setStatus(currentUser.getId(), id, ParticipationStatus.CANCELED);
        System.out.println("Participação cancelada!");

        saveAll(fileRepo, catRepo, eventRepo, partRepo);
    }

    private static void listarMeusConfirmados(EventRepository eventRepo,
                                              ParticipationRepository partRepo,
                                              User currentUser) {
        List<String> ids = partRepo.confirmedEventIds(currentUser.getId());
        if (ids.isEmpty()) {
            System.out.println("Você não tem participações confirmadas.");
            return;
        }
        System.out.println("\n-- Meus eventos confirmados --");
        for (Event e : eventRepo.findAll()) {
            if (ids.contains(e.getId())) {
                System.out.println(
                        e.getId() + " | " + e.getNome() +
                                " | " + DateUtil.formatIso(e.getStart()) +
                                " → " + DateUtil.formatIso(e.getEnd())
                );
            }
        }
    }

    // ====== persistência agregada ======
    private static void saveAll(FileRepository fileRepo,
                                CategoryRepository catRepo,
                                EventRepository eventRepo,
                                ParticipationRepository partRepo) {
        List<String> all = new ArrayList<>();
        all.addAll(catRepo.exportSectionLines());
        all.addAll(eventRepo.exportSectionLines());
        all.addAll(partRepo.exportSectionLines());
        fileRepo.writeAll(all);
    }
    private static void ensureBaseCategories(CategoryRepository catRepo,
                                             FileRepository fileRepo,
                                             EventRepository eventRepo,
                                             ParticipationRepository partRepo) {
        if (catRepo.findAll().isEmpty()) {
            catRepo.add(new Category("c-001", "Show"));
            catRepo.add(new Category("c-002", "Esporte"));
            catRepo.add(new Category("c-003", "Festa"));
            saveAll(fileRepo, catRepo, eventRepo, partRepo);
            System.out.println("Categorias base criadas (Show, Esporte, Festa).");
        }
    }
    private static void cadastrarEvento(Scanner sc,
                                        CategoryRepository catRepo,
                                        EventRepository eventRepo,
                                        FileRepository fileRepo,
                                        ParticipationRepository partRepo) {
        System.out.println("\n-- Cadastrar novo evento --");

        // 1) Nome
        System.out.print("Nome do evento: ");
        String nome = sc.nextLine().trim();
        if (nome.isEmpty()) { System.out.println("Nome é obrigatório."); return; }

        // 2) Endereço
        System.out.print("Endereço: ");
        String endereco = sc.nextLine().trim();
        if (endereco.isEmpty()) { System.out.println("Endereço é obrigatório."); return; }

        // 3) Categoria (lista + escolha por ID)
        System.out.println("Categorias disponíveis:");
        for (Category c : catRepo.findAll()) {
            System.out.println("- " + c.getId() + " | " + c.getNome());
        }
        System.out.print("Digite o ID da categoria: ");
        String categoryId = sc.nextLine().trim();
        boolean catOk = catRepo.findAll().stream().anyMatch(c -> c.getId().equals(categoryId));
        if (!catOk) { System.out.println("Categoria inválida."); return; }

        // 4) Datas (ISO: 2025-08-27T20:00)
        System.out.print("Início (ISO, ex: 2025-08-27T20:00): ");
        String startStr = sc.nextLine().trim();
        System.out.print("Fim (ISO, ex: 2025-08-27T22:00): ");
        String endStr = sc.nextLine().trim();

        try {
            var start = DateUtil.parseIso(startStr);
            var end   = DateUtil.parseIso(endStr);
            if (!end.isAfter(start)) {
                System.out.println("Fim deve ser depois do início.");
                return;
            }

            // 5) Descrição
            System.out.print("Descrição: ");
            String desc = sc.nextLine().trim();

            // 6) Cria ID automático
            String id = java.util.UUID.randomUUID().toString();

            // 7) Salva em memória e persiste no arquivo
            Event e = new Event(id, nome, endereco, categoryId, start, end, desc);
            eventRepo.add(e);
            saveAll(fileRepo, catRepo, eventRepo, partRepo);

            System.out.println("Evento criado com ID: " + id);

        } catch (Exception ex) {
            System.out.println("Data/hora inválida. Use formato ISO, ex: 2025-08-27T20:00");
        }
    }
}
