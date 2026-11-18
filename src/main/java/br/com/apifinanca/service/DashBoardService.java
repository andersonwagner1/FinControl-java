package br.com.apifinanca.service;

import br.com.apifinanca.dto.BasFluxoFinanceiroMensalDto;
import br.com.apifinanca.repository.BasMovimentacaoFinalRepository;
import br.com.apifinanca.repository.BasMovimentacaoRepository;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
public class DashBoardService {

    private final BasMovimentacaoFinalRepository movimentacaoFinalrepository;
    private final BasMovimentacaoRepository movimentacaoRepository;
    private final EntityManager entityManager;

    public DashBoardService(
            BasMovimentacaoFinalRepository movimentacaoFinalrepository,
            BasMovimentacaoRepository movimentacaoRepository,
            EntityManager entityManager
    ) {
        this.movimentacaoFinalrepository = movimentacaoFinalrepository;
        this.movimentacaoRepository = movimentacaoRepository;
        this.entityManager = entityManager;
    }


    @Transactional(readOnly = true)
    public List<BasFluxoFinanceiroMensalDto> listarTabelaMovimentacao(int ano) {
         List<Object[]> movimentacaoFinal = movimentacaoFinalrepository.buscarPorcompetenciaInicial(ano);
       
         

            List<Object[]> listaMovimentacaoMensal = movimentacaoRepository.relatorioDeMOvimentacaoMensalPorAno(ano);
            int i = 0;
            
            String[] meses = {"Janeiro", "Fevereiro", "Março", "Abril","Maio","Junho","Julho","Agosto","Setembro","Outubro","Novembro", "Dezembro"};

            List<BasFluxoFinanceiroMensalDto> listafluxoFinanceiroMensalDto = new ArrayList<>();

            
            for(Object[] o : listaMovimentacaoMensal){
                Double saldoAnterior = Double.parseDouble(movimentacaoFinal.get(i)[1].toString());
                Double saldoFinal = Double.parseDouble(movimentacaoFinal.get(i)[2].toString());
                
               
                Double saldoCreditoTotal = Double.parseDouble(o[3].toString()) + saldoAnterior;
                Double saldoDebitoTotal = Double.parseDouble(o[6].toString());
                Double totalRendimento = Double.parseDouble(o[9].toString()) - Double.parseDouble(o[11].toString());
                Double saldoTotal = saldoCreditoTotal -saldoDebitoTotal+totalRendimento;

//15176.85
                
                BasFluxoFinanceiroMensalDto fluxoFinanceiroMensalDto = BasFluxoFinanceiroMensalDto.builder()
                    .mes(meses[i])
                    .saldoAnterior(saldoAnterior)
                    .credito(Double.parseDouble(o[1].toString()))
                    .renda(Double.parseDouble(o[2].toString()))
                    .totalCredito(Double.parseDouble(o[3].toString()) + saldoAnterior)
                    .mensal(Double.parseDouble(o[4].toString()))
                    .debito(Double.parseDouble(o[5].toString()))
                    .totalDebito(Double.parseDouble(o[6].toString()))
                    .aplicacao(Double.parseDouble(o[7].toString()))
                    .resgate(Double.parseDouble(o[8].toString()))
                    .rendimento(Double.parseDouble(o[9].toString()))
                    .rendimentoNegativo(Double.parseDouble(o[10].toString()))
                    .totalRendimento(Double.parseDouble(o[11].toString()))
                    .saldoFinal(saldoTotal)
                    .saldoFinalMovimentacao(saldoFinal)

                .build();


                i++;
                listafluxoFinanceiroMensalDto.add(fluxoFinanceiroMensalDto);
            }


        return listafluxoFinanceiroMensalDto;
    }




    


   
}
