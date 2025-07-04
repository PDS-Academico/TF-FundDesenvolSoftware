package com.tffds.tf.adaptadores_de_interfaces.persistencia;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.tffds.tf.adaptadores_de_interfaces.persistencia.obj_persistencia.ItemPedido;
import com.tffds.tf.adaptadores_de_interfaces.persistencia.obj_persistencia.Orcamento;
import com.tffds.tf.adaptadores_de_interfaces.persistencia.obj_persistencia.Produto;
import com.tffds.tf.dominio.interfaces_persistencia.InterfaceRepOrcamento;
import com.tffds.tf.dominio.interfaces_persistencia.InterfaceRepProdutos;
import com.tffds.tf.dominio.modelos.ItemPedidoModel;
import com.tffds.tf.dominio.modelos.OrcamentoModel;
import com.tffds.tf.dominio.modelos.ProdutoModel;

@Repository
public class RepOrcamentosJPA implements InterfaceRepOrcamento{
    private InterfaceRepOrcamentosJPA repOrc;
    private InterfaceRepProdutos repProd;

    @Autowired
    public RepOrcamentosJPA(InterfaceRepOrcamentosJPA repOrc, InterfaceRepProdutos repProd) {
        this.repOrc = repOrc;
        this.repProd = repProd;
    }


    @Override
    public List<OrcamentoModel> todos() {
        List<Orcamento> listaOrc = repOrc.findAll(); // recupera todos os Orcamento
        List<OrcamentoModel> listaOrcMod = new ArrayList<>(listaOrc.size()); // cria lista de OrcamentoModel

        if(listaOrc.size() == 0) {
            return new ArrayList<OrcamentoModel>();
        } else {
            for(Orcamento orc : listaOrc){ // para cada Orcamento na lista
                List<ItemPedido> listaPed = orc.getItens(); // recupera a lista de ItemPedido
                List<ItemPedidoModel> listaPedMod = new ArrayList<>(listaPed.size()); // cria uma lista de ItemPedidoModel
                
                for(ItemPedido ped : listaPed) { // para cada ItemPedido
                    ProdutoModel prod = repProd.consultaPorId(ped.getIdProduto()); // obtem o ProdutoModel
                    listaPedMod.add(ItemPedido.toModel(ped, prod)); // transforma ItemPedido -> ItemPedodoModel
                }

                listaOrcMod.add(Orcamento.toModel(orc, listaPedMod)); // transforma Orcamento -> OrcamentoModel
            }

            return listaOrcMod;
        }
    }

    @Override
    public void cadastra(OrcamentoModel orcamento) {
        Orcamento orc = Orcamento.fromModel(orcamento);
        while(repOrc.findById(orc.getId()).isPresent()) {orc.incId();}
        repOrc.save(orc);
    }

    @Override
    public OrcamentoModel recuperaPorId(long id) {
        Optional<Orcamento> orc = repOrc.findById(id);
        if(orc.isEmpty()) {
            return null;
        } else {
            List<ItemPedido> listaPed = orc.get().getItens(); 
            List<ItemPedidoModel> listaPedMod = new ArrayList<>(listaPed.size()); 
            
            for(ItemPedido ped : listaPed) { 
                ProdutoModel prod = repProd.consultaPorId(ped.getIdProduto()); 
                listaPedMod.add(ItemPedido.toModel(ped, prod));
            }

            return Orcamento.toModel(orc.get(), listaPedMod);
        }
    }

    @Override
    public void marcaComoEfetivado(long id) {
        Optional<Orcamento> orc = repOrc.findById(id);
        if(!orc.isEmpty()) {
            return;
        }
        else{
            orc.get().setEfetivado(true);
            repOrc.save(orc.get());
        }
    }

}
