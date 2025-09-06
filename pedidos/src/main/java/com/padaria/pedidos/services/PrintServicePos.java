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


    public void imprimirRecibo(Pedido pedido) throws IOException {

        PrintService printService = PrinterOutputStream.getPrintServiceByName(nomeImpressora);

        if (printService == null) {
            throw new IOException("Impressora não encontrada: " + nomeImpressora);
        }
        try (PrinterOutputStream printerOutputStream = new PrinterOutputStream(printService);
             EscPos escpos = new EscPos(printerOutputStream)) {

            escpos.setCharacterCodeTable(EscPos.CharacterCodeTable.WPC1252);

            Style titleStyle = new Style()
                    .setFontSize(Style.FontSize._2, Style.FontSize._2)
                    .setJustification(EscPosConst.Justification.Center);

            escpos.writeLF(titleStyle, "Padaria Tradição")
                    .writeLF( "Data: " + java.time.LocalDate.now())
                    .feed(2)
                    .write("Cliente: " + pedido.getNomeCliente() + " " + pedido.getContato())
                    .feed(1)
                    .writeLF("-------------------------------------------")
                    .writeLF(pedido.getProduto() +" UN/KG: " + pedido.getQuantidade()) // O conteúdo dinâmico vindo da API
                    .feed(1)
                    .writeLF("Observação: ")
                    .writeLF(pedido.getDescricao())
                    .writeLF("-------------------------------------------")
                    .writeLF( "Entrega:  " +pedido.getDataHora().format(formatadorData) + " - " + pedido.getDataHora().format(formatadorHora))
                    .feed(3)
                    .cut(EscPos.CutMode.FULL); // Corta o papel

        } catch (Exception e) {
            // Trate a exceção de forma adequada (log, etc.)
            throw new IOException("Erro ao tentar imprimir: " + e.getMessage(), e);
        }
    }
}


                    //.feed(2)
                   //.writeLF(new Style().setJustification(EscPosConst.Justification.Center), "Obrigado pela preferência!")