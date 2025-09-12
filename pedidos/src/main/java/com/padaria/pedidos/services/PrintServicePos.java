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
    DateTimeFormatter formatadorDataHora = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final int size3Leng = 14;
    private final int size2Leng = 21;
    private final int size1Leng = 40;


    private void escreverComQuebraLinha(EscPos escpos, String texto, int limiteCaracteres, Style style) throws IOException {
        String[] palavras = texto.split(" ");
        StringBuilder linhaAtual = new StringBuilder();
        try{
        for (String palavra : palavras) {
            if (linhaAtual.length() + palavra.length() + 1 <= limiteCaracteres) {
                if (linhaAtual.length() > 0) {
                    linhaAtual.append(" ");
                }
                linhaAtual.append(palavra);
            } else {
                escpos.writeLF(style ,linhaAtual.toString());
                linhaAtual = new StringBuilder(palavra);
            }
        }
        if (linhaAtual.length() > 0) {
            escpos.writeLF(style, linhaAtual.toString());
        }
        } catch (IOException e) {
            throw new IOException(e);
        }
    }

    Style greatStyle = new Style()
            .setFontSize(Style.FontSize._3, Style.FontSize._3)
            .setJustification(EscPosConst.Justification.Center);
    Style titleStyle = new Style()
            .setFontSize(Style.FontSize._2, Style.FontSize._2)
            .setJustification(EscPosConst.Justification.Center);
    Style centerStyle = new Style().setJustification(EscPosConst.Justification.Center);
    Style commom = new Style();

    public void imprimirRecibo(Pedido pedido) throws IOException {
        PrintService printService = PrinterOutputStream.getPrintServiceByName(nomeImpressora);
        String[] palavras = pedido.getDescricao().split(" ");
        StringBuilder linhaAtual = new StringBuilder();

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
                    .writeLF(pedido.getProduto() +" UN/KG: " + pedido.getQuantidade())
                    .feed(1)
                    .writeLF("Observação: ");

                    escreverComQuebraLinha(escpos, pedido.getDescricao(), size1Leng, commom);

                    escpos
                    .writeLF("------------------------------------------")
                    .writeLF( "Entrega:  " +pedido.getDataHora().format(formatadorData) + " - " + pedido.getDataHora().format(formatadorHora))
                    .writeLF( "Cliente:  " +pedido.getNomeCliente() + " - " + pedido.getContato())
                    .feed(3)
                    .cut(EscPos.CutMode.FULL);

        } catch (IOException e) {
            throw new IOException("Erro ao tentar imprimir: " + e.getMessage(), e);
        }
    }

    public void imprimirViaProducao(Pedido pedido) throws IOException{
        PrintService printService = PrinterOutputStream.getPrintServiceByName(nomeImpressora);
        String[] palavras = pedido.getDescricao().split(" ");
        StringBuilder linhaAtual = new StringBuilder();
        if (printService == null) {
            throw new IOException("Impressora não encontrada: " + nomeImpressora);
        }
        try (PrinterOutputStream printerOutputStream = new PrinterOutputStream(printService);
             EscPos escpos = new EscPos(printerOutputStream)) {

            escpos.setCharacterCodeTable(EscPos.CharacterCodeTable.WPC1252);

            escreverComQuebraLinha(escpos, pedido.getProduto(), size3Leng, greatStyle);
                    escpos.feed(1)
                    .writeLF(titleStyle ,  pedido.getQuantidade() + "KG ou Unidade\n"
                            +pedido.getDataHora().format(formatadorDataHora))
                    .writeLF("------------------------------------------")
                    .writeLF("Observação: ");

            escreverComQuebraLinha(escpos, pedido.getDescricao(), size2Leng, titleStyle);

            escpos.writeLF("------------------------------------------")
                    .writeLF(centerStyle,"Hora da entrega: " + pedido.getDataHora().format(formatadorHora))
                    .writeLF(centerStyle,"Cliente:  " +pedido.getNomeCliente() + " - " + pedido.getContato())
                    .feed(3)
                    .cut(EscPos.CutMode.FULL);

        } catch (IOException e) {
            throw new IOException("Erro ao tentar imprimir: " + e.getMessage(), e);
        }
    }
}



