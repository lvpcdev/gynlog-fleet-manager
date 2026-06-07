# GynLog Fleet Manager

## 📋 Sobre o Projeto

O **GynLog Fleet Manager** é uma aplicação *Desktop* desenvolvida em **Java** com o padrão de arquitetura **Model-View-Controller (MVC)**. O projeto foi concebido com o objetivo de fornecer uma solução robusta para o **controle financeiro e operacional de frotas de veículos**.

Este projeto possui um caráter **acadêmico**, demonstrando a aplicação de conceitos de programação orientada a objetos, design patterns (MVC) e persistência de dados em um ambiente de aplicação *Desktop*.

### 🎯 Funcionalidades Principais

A aplicação oferece um conjunto de funcionalidades essenciais para a gestão de frotas:

*   **Controle de Veículos:** Cadastro, consulta e gestão do status da frota.
*   **Gestão de Movimentações:** Registro de entradas e saídas financeiras relacionadas à frota.
*   **Controle de Despesas:** Cadastro e categorização de tipos de despesas.
*   **Geração de Relatórios:** Emissão de relatórios operacionais e financeiros para análise.
*   **Persistência de Dados:** Os dados são armazenados de forma local em arquivos de texto, simulando um sistema de persistência simples e eficaz.

## 🛠️ Tecnologias Utilizadas

O projeto foi construído utilizando as seguintes tecnologias e ferramentas:

| Tecnologia | Descrição |
| :--- | :--- |
| **Java** | Linguagem de programação principal. |
| **Maven** | Ferramenta de automação de *build* e gerenciamento de dependências. |
| **MVC (Model-View-Controller)** | Padrão de arquitetura para separação de responsabilidades. |
| **Swing/AWT** | Bibliotecas nativas do Java para construção da interface gráfica (*Desktop*). |
| **FlatLaf** | Biblioteca de *look and feel* para modernização da interface gráfica. |

## 📂 Estrutura do Projeto

A estrutura de diretórios reflete o padrão MVC, garantindo a separação lógica entre as camadas da aplicação:

```
gynlog-fleet-manager/
├── data/                 # Arquivos de persistência de dados (TXT)
│   ├── despesas/
│   ├── movimentacoes/
│   └── veiculos/
├── src/
│   ├── main/
│   │   └── java/
│   │       ├── controller/   # Lógica de controle e manipulação de dados
│   │       ├── model/        # Entidades (Movimentacao, Veiculo, TipoDespesa) e Enums
│   │       ├── persistence/  # Camada de acesso a dados (DAOs)
│   │       └── view/         # Interfaces gráficas (GUI)
│   └── test/
├── pom.xml               # Arquivo de configuração do Maven
└── LICENSE               # Licença do projeto (MIT)
```

## 🚀 Como Executar

Para rodar o projeto em sua máquina local, siga os passos abaixo.

### Pré-requisitos

Certifique-se de ter as seguintes ferramentas instaladas:

*   **Java Development Kit (JDK)**: Versão 8 ou superior.
*   **Apache Maven**: Para gerenciar as dependências e compilar o projeto.
*   **Git**: Para clonar o repositório.

### Instalação e Execução

1.  **Clone o repositório:**
    ```bash
    git clone https://github.com/lvpcdev/gynlog-fleet-manager.git
    cd gynlog-fleet-manager
    ```

2.  **Compile o projeto com Maven:**
    ```bash
    mvn clean install
    ```
    Este comando irá baixar as dependências e compilar o código-fonte, gerando o arquivo `.jar` na pasta `target/`.

3.  **Execute a aplicação:**
    O arquivo JAR gerado pode ser executado diretamente. O nome exato do arquivo pode variar, mas geralmente segue o padrão `gynlog-fleet-manager-X.Y.Z.jar`.
    ```bash
    java -jar target/gynlog-fleet-manager-1.0-SNAPSHOT.jar
    ```
    *(Nota: O nome do arquivo JAR pode precisar ser ajustado para o nome exato gerado pelo Maven.)*

## 💾 Persistência de Dados

É importante notar que a aplicação utiliza um sistema de persistência baseado em arquivos de texto simples, localizados no diretório `data/`.

*   Cada entidade (Veículo, Movimentação, TipoDespesa) possui seu próprio arquivo de texto para armazenamento.
*   Os arquivos `*UltimoId.txt` são utilizados para controlar o ID sequencial das entidades.

## 📄 Licença

Este projeto está licenciado sob a **Licença MIT**. Consulte o arquivo [LICENSE](LICENSE) para mais detalhes.

## 👥 Contribuidores

O projeto acadêmico foi desenvolvido pelos seguintes colaboradores:

*   **Lucas Vicente**
*   **Rafael Camargo**
*   **Arthur Caetano**
*   **Ruan Carlos**
*   **Davi Fraga**
