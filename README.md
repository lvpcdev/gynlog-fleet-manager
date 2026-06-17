# GynLog Fleet Manager

## 📋 Sobre o Projeto

O **GynLog Fleet Manager** é uma aplicação *Desktop* desenvolvida em **Java** para o controle de gastos da frota veicular da empresa GynLog. O projeto foi concebido como Projeto Integrador do curso de Engenharia de Software (3º Período), aplicando conceitos de programação orientada a objetos, arquitetura em camadas, estruturas de dados e persistência de dados em arquivo texto.

### 🎯 Funcionalidades Principais

* **Controle de Veículos:** cadastro, edição, busca por placa/modelo e gestão de status (ativo/inativo).
* **Controle de Tipos de Despesa:** cadastro, edição e gestão de status (ativo/inativo).
* **Gestão de Movimentações (Despesas):** registro de despesas por veículo, com data, valor, descrição e, para despesas de combustível, quilometragem e litros abastecidos.
* **Fila de Aprovação:** toda movimentação cadastrada (ou editada) entra como **PENDENTE** em uma fila de aprovação (estrutura de dados própria, FIFO) e só passa a contar nos relatórios após ser **aprovada**. Movimentações rejeitadas são excluídas.
* **Relatórios Gerenciais:** 10 relatórios distintos, incluindo despesas por veículo, totais mensais, totais de combustível, IPVA por ano, veículos inativos, multas por veículo, média de despesas por categoria, consumo médio (km/L), custo médio de IPVA e identificação do veículo com maior/menor custo de combustível.
* **Exportação para Planilha:** exportação de veículos, tipos de despesa e movimentações para arquivos `.csv`, compatíveis com Excel/LibreOffice.
* **Persistência de Dados:** armazenamento local em arquivos de texto (`.txt`), sem uso de banco de dados.

## 🛠️ Tecnologias Utilizadas

| Tecnologia | Descrição |
| :--- | :--- |
| **Java 21 (JDK)** | Linguagem de programação principal. |
| **Apache Maven** | Ferramenta de automação de *build* e gerenciamento de dependências. |
| **Java Swing** | Biblioteca nativa do Java para construção da interface gráfica (*Desktop*). |
| **FlatLaf 3.4** | Biblioteca de *look and feel* para modernização da interface gráfica. |
| **Git/GitHub** | Controle de versão. |

## 📂 Estrutura do Projeto

A estrutura segue uma arquitetura em camadas (View → Controller → Service → DAO), com estruturas de dados e utilitários isolados em `util`:

```
gynlog-fleet-manager/
├── data/                    # Arquivos de persistência (TXT)
│   ├── veiculos/
│   ├── despesas/
│   └── movimentacoes/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   ├── controller/   # Ponte entre a interface e os services
│   │   │   ├── service/      # Regras de negócio e validações
│   │   │   ├── dao/          # Persistência em arquivo texto (CRUD)
│   │   │   ├── model/
│   │   │   │   ├── entities/ # Veiculo, TipoDespesa, Movimentacao
│   │   │   │   └── enums/    # StatusVeiculo, StatusTipoDespesa, StatusMovimentacao
│   │   │   ├── view/
│   │   │   │   ├── gui/      # Telas Swing (MenuView, VeiculosView, DespesasView, RelatoriosView)
│   │   │   │   └── util/     # Componentes auxiliares de interface (DatePicker, filtros)
│   │   │   ├── util/         # Fila (lista encadeada), SelectionSort, BuscaSequencial, CsvExporter
│   │   │   └── exceptions/   # Exceções customizadas da aplicação
│   │   └── resources/        # Logomarca e demais recursos estáticos
│   └── test/
├── pom.xml                   # Configuração do Maven
└── LICENSE
```

## 🧩 Estruturas de Dados Implementadas

Como parte da disciplina de Estrutura de Dados I, o projeto implementa manualmente (sem uso de bibliotecas prontas do Java Collections para essas finalidades):

* **Fila (lista encadeada própria — `Fila<T>`/`No<T>`):** utilizada para controlar as movimentações pendentes de aprovação, respeitando a ordem de cadastro (FIFO).
* **Algoritmo de ordenação manual (`SelectionSort`):** utilizado para ordenar veículos por custo total de combustível, identificando o de maior e o de menor gasto.
* **Busca sequencial (`BuscaSequencial`):** utilizada para localizar veículos por placa ou modelo na tela de cadastro de veículos.

## 🚀 Como Executar

### Pré-requisitos

* **JDK 21** ou superior.
* **Apache Maven**.
* **Git** (para clonar o repositório).

### Instalação e Execução

1. **Clone o repositório:**
   ```bash
   git clone https://github.com/lvpcdev/gynlog-fleet-manager.git
   cd gynlog-fleet-manager
   ```

2. **Compile o projeto com Maven:**
   ```bash
   mvn clean package
   ```
   Este comando baixa as dependências (FlatLaf) e compila o código-fonte, gerando o `.jar` na pasta `target/`.

3. **Execute a aplicação:**
   ```bash
   java -jar target/gynlog-fleet-manager-1.0-SNAPSHOT.jar
   ```
   *(O nome exato do arquivo `.jar` pode variar conforme a versão definida no `pom.xml`.)*

   Alternativamente, a classe `Main` pode ser executada diretamente pela IDE (IntelliJ IDEA recomendado).

## 💾 Persistência de Dados

A aplicação utiliza um sistema de persistência baseado em arquivos de texto simples, localizados no diretório `data/`, sem uso de banco de dados:

* Cada entidade (`Veiculo`, `TipoDespesa`, `Movimentacao`) possui seu próprio arquivo de dados (`.txt`) e um arquivo auxiliar de controle de ID (`*UltimoId.txt`).
* Os arquivos de dados podem estar vazios (sem registros).
* Os dados podem ser exportados para `.csv` a qualquer momento pelas telas de Veículos, Tipos de Despesa e Histórico de Despesas, permitindo sua manipulação em planilhas eletrônicas (Excel/LibreOffice Calc).

## 📄 Licença

Este projeto está licenciado sob a **Licença MIT**. Consulte o arquivo [LICENSE](LICENSE) para mais detalhes.

## 👥 Contribuidores

Projeto acadêmico desenvolvido pelos seguintes colaboradores:

* **Lucas Vicente**
* **Samuel Tavares**
* **Rafael Camargo**
* **Arthur Caetano**
* **Ruan Carlos**
* **Davi Fraga**
