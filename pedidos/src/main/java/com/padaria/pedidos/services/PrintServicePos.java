package com.padaria.pedidos.services;

import com.github.anastaciocintra.escpos.EscPos;
import com.github.anastaciocintra.escpos.EscPosConst;
import com.github.anastaciocintra.escpos.Style;
import com.github.anastaciocintra.output.PrinterOutputStream;
import com.padaria.pedidos.model.Pedido;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.print.PrintService;
import java.io.IOException;
import java.time.format.DateTimeFormatter;

@Service
public class PrintServicePos {

    @Value("${impressora.nome}")
    private String nomeImpressora;
    DateTimeFormatter formatadorData = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    DateTimeFormatter formatadorHora = DateTimeFormatter.ofPattern("HH:mm");
    PrintService printService = PrinterOutputStream.getPrintServiceByName(nomeImpressora);
    StringBuilder linhaAtual = new StringBuilder();
    Style titleStyle = new Style()
            .setFontSize(Style.FontSize._2, Style.FontSize._2)
            .setJustification(EscPosConst.Justification.Center);
    Style centerStyle = new Style().setJustification(EscPosConst.Justification.Center);

    public void imprimirRecibo(Pedido pedido) throws IOException {

        String[] palavras = pedido.getDescricao().split(" ");

        if (printService == null) {
            throw new IOException("Impressora não encontrada: " + nomeImpressora);
        }
        try (PrinterOutputStream printerOutputStream = new PrinterOutputStream(printService);
             EscPos escpos = new EscPos(printerOutputStream)) {

            escpos.setCharacterCodeTable(EscPos.CharacterCodeTable.WPC1252);

            escpos.writeLF(titleStyle, "Padaria Tradição")
                    .feed(1)
                    .writeLF(centerStyle, java.time.LocalDate.now().format(formatadorData))
                    .feed(1)
                    .write(centerStyle,"Contato: (31) 9 8267-2984")
                    .feed(1)
                    .writeLF("------------------------------------------")
                    .writeLF(pedido.getProduto() +" UN/KG: " + pedido.getQuantidade()) // O conteúdo dinâmico vindo da API
                    .feed(1)
                    .writeLF("Observação: ");

                    //Laço para quebrar linhas em campos maiores que 40chars
                    for (String palavra : palavras) {
                        if (linhaAtual.length() + palavra.length() + 1 <= 40) {
                            if (linhaAtual.length() > 0) {
                                linhaAtual.append(" ");
                            }
                            linhaAtual.append(palavra);
                        } else {
                            escpos.writeLF(linhaAtual.toString());
                            linhaAtual = new StringBuilder(palavra);
                        }
                    }
                    if (linhaAtual.length() > 0) {
                        escpos.writeLF(linhaAtual.toString());
                    }

                    escpos
                    .writeLF("------------------------------------------")
                    .writeLF( "Entrega:  " +pedido.getDataHora().format(formatadorData) + " - " + pedido.getDataHora().format(formatadorHora))
                    .writeLF( "Cliente:  " +pedido.getNomeCliente() + " - " + pedido.getContato())
                    .feed(3)
                    .cut(EscPos.CutMode.FULL);

        } catch (Exception e) {
            throw new IOException("Erro ao tentar imprimir: " + e.getMessage(), e);
        }


    }

    public void imprimirViaProducao(Pedido pedido) throws IOException{
        String[] palavras = pedido.getDescricao().split(" ");
        if (printService == null) {
            throw new IOException("Impressora não encontrada: " + nomeImpressora);
        }
        try (PrinterOutputStream printerOutputStream = new PrinterOutputStream(printService);
             EscPos escpos = new EscPos(printerOutputStream)) {

            escpos.setCharacterCodeTable(EscPos.CharacterCodeTable.WPC1252);

            escpos.writeLF(titleStyle, pedido.getProduto())
                    .feed(1)
                    .writeLF(titleStyle ,pedido.getDataHora().format(formatadorData))
                    .writeLF("------------------------------------------")
                    .writeLF("Observação: ");

            for (String palavra2 : palavras) {
                if (linhaAtual.length() + palavra2.length() + 1 <= 40) {
                    if (linhaAtual.length() > 0) {
                        linhaAtual.append(" ");
                    }
                    linhaAtual.append(palavra2);
                } else {
                    escpos.writeLF(linhaAtual.toString());
                    linhaAtual = new StringBuilder(palavra2);
                }
            }
            if (linhaAtual.length() > 0) {
                escpos.writeLF(linhaAtual.toString());
            }
            escpos.writeLF("------------------------------------------")
                    .writeLF("Hora da entrega: " + pedido.getDataHora().format(formatadorHora))
                    .writeLF( "Cliente:  " +pedido.getNomeCliente() + " - " + pedido.getContato())
                    .feed(3)
                    .cut(EscPos.CutMode.FULL);


        } catch (Exception e) {
            throw new IOException("Erro ao tentar imprimir: " + e.getMessage(), e);
        }


    }
}



