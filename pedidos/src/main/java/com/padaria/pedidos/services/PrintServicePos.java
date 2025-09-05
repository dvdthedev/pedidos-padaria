package com.padaria.pedidos.services;

import com.github.anastaciocintra.escpos.EscPos;
import com.github.anastaciocintra.escpos.EscPosConst;
import com.github.anastaciocintra.escpos.Style;
import com.github.anastaciocintra.output.PrinterOutputStream;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.print.PrintService;
import java.io.IOException;

@Service
public class PrintServicePos {

    @Value("${impressora.nome}")
    private String nomeImpressora;

    public void imprimirRecibo(String corpoRecibo) throws IOException {
        
        PrintService printService = PrinterOutputStream.getPrintServiceByName(nomeImpressora);

        if (printService == null) {
            throw new IOException("Impressora não encontrada: " + nomeImpressora);
        }
        try (PrinterOutputStream printerOutputStream = new PrinterOutputStream(printService);
             EscPos escpos = new EscPos(printerOutputStream)) {

            // Exemplo de como usar a API fluente do escpos-coffee
            Style titleStyle = new Style()
                    .setFontSize(Style.FontSize._2, Style.FontSize._2)
                    .setJustification(EscPosConst.Justification.Center);

            escpos.writeLF(titleStyle, "MEU RESTAURANTE")
                    .feed(2)
                    .write("Cliente: " + "Fulano de Tal")
                    .writeLF("Data: " + java.time.LocalDate.now())
                    .feed(1)
                    .writeLF("----------------------------------------")
                    .writeLF(corpoRecibo) // O conteúdo dinâmico vindo da API
                    .writeLF("----------------------------------------")
                    .feed(2)
                    .writeLF(new Style().setJustification(EscPosConst.Justification.Center), "Obrigado pela preferência!")
                    .feed(3)
                    .cut(EscPos.CutMode.FULL); // Corta o papel

        } catch (Exception e) {
            // Trate a exceção de forma adequada (log, etc.)
            throw new IOException("Erro ao tentar imprimir: " + e.getMessage(), e);
        }
    }
}