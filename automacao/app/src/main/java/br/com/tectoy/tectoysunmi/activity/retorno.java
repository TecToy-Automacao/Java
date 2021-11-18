package br.com.tectoy.tectoysunmi.activity;


class RetornoMsiTef {
    private String CODRESP;
    private String COMP_DADOS_CONF;
    private String CODTRANS;
    private String VLTROCO;
    private String REDE_AUT;
    private String BANDEIRA;
    private String NSU_SITEF;
    private String NSU_HOST;
    private String COD_AUTORIZACAO;
    private String TIPO_PARC;
    private String NUM_PARC;
    private String VIA_ESTABELECIMENTO;
    private String VIA_CLIENTE;

    public String getNSUHOST (){
        return this.NSU_HOST;
    }
    public String getSitefTipoParcela(){
        return this.TIPO_PARC;
    }
    public String getNSUSitef(){
        return this.NSU_SITEF;
    }
    public String getCodTrans (){
        return this.CODTRANS;
    }
    public void setCodTrans (String _cODTRANS){
        this.CODTRANS= _cODTRANS;
    }
    public String getNameTransCod (){
        String retorno = "Valor invalido";
        switch (this.TIPO_PARC) {
            case "00":
                retorno = "A vista";
                break;
            case "01":
                retorno = "Pré-Datado";
                break;
            case "02":
                retorno = "Parcelado Loja";
                break;
            case "03":
                retorno = "Parcelado Adm";
                break;
        }
        return retorno;
    }

    public String getvlTroco (){
        return this.VLTROCO;
    }
    public String  getParcelas (){
        if (this.NUM_PARC != null) {
            return this.NUM_PARC;
        }
        return "";
    }

    public String getCodAutorizacao (){
        return this.COD_AUTORIZACAO;
    }
    public String textoImpressoEstabelecimento ()
    {
        return this.VIA_ESTABELECIMENTO;
    }
    public String textoImpressoCliente () { return this.VIA_CLIENTE;}
    public String getCompDadosConf (){return this.COMP_DADOS_CONF;}
    public String getCodResp (){return this.CODRESP;}
    public String getRedeAut (){return this.REDE_AUT;}
    public String getBandeira (){return this.BANDEIRA;}

}

