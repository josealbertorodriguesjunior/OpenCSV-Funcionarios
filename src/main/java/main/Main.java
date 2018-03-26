/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import helper.ConnectionFactory;
import static helper.ConnectionFactory.createConnection;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author junior
 */
public class Main extends ConnectionFactory {

    private static final String SAMPLE_CSV_FILE_PATH = "./funcionarios.csv";

    public static void main(String[] args) throws IOException, SQLException {
        try (
                Reader reader = Files.newBufferedReader(Paths.get(SAMPLE_CSV_FILE_PATH));
                CSVReader csvReader = new CSVReaderBuilder(reader).withSkipLines(1).build();) {

            List<String[]> records = csvReader.readAll();

            Connection conexao = createConnection();

            for (int i = 1; i < records.size(); i++) {

                String[] get = records.get(i);
                
                String check = "SELECT tbusuarios_login FROM tbusuarios WHERE tbusuarios_login = '" +get[8] + "'";
                PreparedStatement pstm = conexao.prepareStatement(check);
                ResultSet rs = pstm.executeQuery();
                
                if(!rs.next()){
                    //Insere tbpessoas
                    String sexo;
                    if(get[44].equalsIgnoreCase("masculino")){
                        sexo = "M";
                    } else {
                        sexo = "F";
                    }
                    String sqlInsertPessoa = "insert into tbpessoas(tbpessoas_nome,tbpessoas_sexo,tbpessoas_ufEnd,tbpessoas_cidade,tbpessoas_endereco,tbpessoas_endereconumero,tbpessoas_bairro,"
                            + "tbpessoas_cep,tbpessoas_dataCadastro,tbpessoas_ativo,tbpessoas_cpf,tbpessoas_dataNasc,tbpessoas_ufNasc,tbpessoas_rg,tbpessoas_ufRg,tbpessoas_mae) "
                            + "values('"+get[1]+"','"+sexo+"',26,'"+get[42]+"','"+get[10]+"','"+get[17]+"','"+get[11]+"','"+get[12]+"',now(),'S','"+get[8]+"','"+get[14]+"',26,'"+get[7]+"',26,'"+get[13]+"')";
                    PreparedStatement psInsertPessoas = conexao.prepareStatement(sqlInsertPessoa);
                    System.out.println(i +" - "+psInsertPessoas);
                    psInsertPessoas.executeUpdate();
                    
                    //Insere tbusuarios
                    String sqlInsertUsuarios = "insert into tbusuarios(tbusuarios_login,tbusuarios_senha,tbPerfil_id,tbusuarios_ativo,tbusuarios_tipo) "
                            + "values('"+get[8]+"','1234mudar',11,'S','F')";
                    PreparedStatement psInsertUsuarios = conexao.prepareStatement(sqlInsertUsuarios);
                    System.out.println(i +" - "+psInsertUsuarios);
                    psInsertUsuarios.executeUpdate();
                    
                    //Insere Funcionarios                    
                    String sqlInsertFuncionarios = "insert into tbfuncionarios(tbcargos_id, tbfuncionarios_matricula,tbpessoas_id,tbusuarios_id,tbescolas_id,tbperfis_id,tbfuncionarios_ativo) "
                            + "values((select tbcargos_id from tbcargos where tbcargos_descricao like '%"+get[38].replace("EMEI", "")+"%'),'"+get[0]+"',(select max(tbpessoas_id) from tbpessoas),(select max(tbusuarios_id) from tbusuarios),"
                            + "(select tbescolas_id from tbescolas where tbescolas_descricao like '%"+get[50].replace("EMEI", "")+"%'),11,'S')";
                    PreparedStatement psInsertFuncionario = conexao.prepareStatement(sqlInsertFuncionarios);
                    System.out.println(i +" - "+psInsertFuncionario);
                    psInsertFuncionario.executeUpdate();
                }
                
            }
        }
    }
}
