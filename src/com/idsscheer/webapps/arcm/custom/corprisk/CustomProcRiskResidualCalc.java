package com.idsscheer.webapps.arcm.custom.corprisk;

public class CustomProcRiskResidualCalc {
	
	private String riskClass1Line;
	private String riskClass2line;
	private String riskClass3line;
	private String riskClassFinal;
	private String riscoPotencial;
	private String riskResidualFinal;
	private String riskName;
	

	public CustomProcRiskResidualCalc(String riskName, String riscoPotencial, String riskClass1Line, String riskClass2line, String riskClass3line) {
		// TODO Auto-generated constructor stub
		this.riskClass1Line = riskClass1Line;
		this.riskClass2line = riskClass2line;
		this.riskClass3line = riskClass3line;
		this.riscoPotencial = riscoPotencial;
		this.riskName = riskName;
	}
	
	public CustomProcRiskResidualCalc(String riskName, String riscoPotencial, String riskClassFinal) {
		// TODO Auto-generated constructor stub
		this.riscoPotencial = riscoPotencial;
		this.riskClassFinal = riskClassFinal;
		this.riskName = riskName;
	}
	
	public void calculateClassFinal() throws CustomCorpRiskException{
		
		if(null == this.riscoPotencial){
			throw new CustomCorpRiskException("O risco de processo \"" + this.riskName + "\" n�o est� avaliado!");
		}
		
		try{
			if(this.riskClass1Line.equals("")){
				if((!this.riskClass2line.equals("")) && (this.riskClass3line.equals(""))){
					this.riskClassFinal = this.riskFinalClassification("", this.riskClass2line, "");
				}else{
					if((this.riskClass2line.equals("")) && (!this.riskClass3line.equals(""))){
						this.riskClassFinal = this.riskFinalClassification("", "", this.riskClass3line);
					}else{
						this.riskClassFinal = this.riskFinalClassification("", this.riskClass2line, this.riskClass3line);
					}
				}
			}else{
				if((!this.riskClass2line.equals("")) && (this.riskClass3line.equals(""))){
					this.riskClassFinal = this.riskFinalClassification(this.riskClass1Line, this.riskClass2line, "");
				}else{
					if((this.riskClass2line.equals("")) && (!this.riskClass3line.equals(""))){
						this.riskClassFinal = this.riskFinalClassification(this.riskClass1Line, "", this.riskClass3line);
					}else{
						this.riskClassFinal = this.riskFinalClassification(this.riskClass1Line, this.riskClass2line, this.riskClass3line);
					}
				}
			}
		}catch(Exception ex){
			throw new CustomCorpRiskException("Erro ao calcular Classifica��o Final " + ex.getMessage());
		}
	}
	
	public void calculateResidualFinal() throws CustomCorpRiskException{
		if(null == this.riscoPotencial){
			throw new CustomCorpRiskException("O risco de processo \"" + this.riskName + "\" n�o est� avaliado!");
		}
		this.riskResidualFinal = this.riskResidualFinal(this.riscoPotencial, this.riskClassFinal);
	}
	
	public String getResidualFinal(){
		return this.riskResidualFinal;
	}
	
	private String riskFinalClassification(String risk1line, String risk2line, String risk3line) throws Exception{
		
		int height_1line = 0;
		int height_2line = 0;
		int height_3line = 0;
		String riskClassFinal = "";
		
		try{
			
			if(!risk1line.equals("")){
				//Classifica��o - Amb. Controles 1a Linha
				if(risk1line.equalsIgnoreCase("Muito Alto"))
					height_1line = 4;
				if(risk1line.equalsIgnoreCase("Alto"))
					height_1line = 3;
				if(risk1line.equalsIgnoreCase("M�dio"))
					height_1line = 2;
				if(risk1line.equalsIgnoreCase("Baixo"))
					height_1line = 1;
			}
			
			if(!risk2line.equals("")){
				//Classifica��o - Amb. Controles 2a Linha
				if(risk2line.equalsIgnoreCase("Muito Alto"))
					height_2line = 4;
				if(risk2line.equalsIgnoreCase("Alto"))
					height_2line = 3;
				if(risk2line.equalsIgnoreCase("M�dio"))
					height_2line = 2;
				if(risk2line.equalsIgnoreCase("Baixo"))
					height_2line = 1;
			}
			
			if(!risk3line.equals("")){
				//Classifica��o - Amb. Controles 3a Linha
				if(risk3line.equalsIgnoreCase("Muito Alto"))
					height_3line = 4;
				if(risk3line.equalsIgnoreCase("Alto"))
					height_3line = 3;
				if(risk3line.equalsIgnoreCase("M�dio"))
					height_3line = 2;
				if(risk3line.equalsIgnoreCase("Baixo"))
					height_3line = 1;		
			}
			
			int maxHeightCtrl = Math.max(height_1line, Math.max(height_2line, height_3line));
			
			switch(maxHeightCtrl){
			case 4:
				riskClassFinal = "Muito Alto";
				break;
			case 3:
				riskClassFinal = "Alto";
				break;
			case 2:
				riskClassFinal = "M�dio";
				break;
			case 1:
				riskClassFinal = "Baixo";
				break;
			default:
				riskClassFinal = "N�o Avaliado";
				break;
			}
			
			return riskClassFinal;
		
		}catch(Exception e){
			throw e;
		}
		
	}
	
	private String riskResidualFinal(String riskPotencial, String riskControlFinal){
		
		String riskResidualReturn = "";
		
		if(riskPotencial.equals("Nao Avaliado"))
			return "N�o Avaliado";
			
		if(riskPotencial.equals("Muito Alto") && riskControlFinal.equals("Muito Alto"))
			riskResidualReturn = "Muito Alto";
		
		if(riskPotencial.equals("Muito Alto") && riskControlFinal.equals("Alto"))
			riskResidualReturn = "Muito Alto";
		
		if(riskPotencial.equals("Muito Alto") && riskControlFinal.equals("M�dio"))
			riskResidualReturn = "Alto";
		
		if(riskPotencial.equals("Muito Alto") && riskControlFinal.equals("Baixo"))
			riskResidualReturn = "M�dio";
		
		if(riskPotencial.equals("Alto") && riskControlFinal.equals("Muito Alto"))
			riskResidualReturn = "Alto";
		
		if(riskPotencial.equals("Alto") && riskControlFinal.equals("Alto"))
			riskResidualReturn = "Alto";
		
		if(riskPotencial.equals("Alto") && riskControlFinal.equals("M�dio"))
			riskResidualReturn = "M�dio";
		
		if(riskPotencial.equals("Alto") && riskControlFinal.equals("Baixo"))
			riskResidualReturn = "M�dio";
		
		if(riskPotencial.equals("M�dio") && riskControlFinal.equals("Muito Alto"))
			riskResidualReturn = "M�dio";
		
		if(riskPotencial.equals("M�dio") && riskControlFinal.equals("Alto"))
			riskResidualReturn = "M�dio";
		
		if(riskPotencial.equals("M�dio") && riskControlFinal.equals("M�dio"))
			riskResidualReturn = "M�dio";
		
		if(riskPotencial.equals("M�dio") && riskControlFinal.equals("Baixo"))
			riskResidualReturn = "Baixo";
		
		if(riskPotencial.equals("Baixo") && riskControlFinal.equals("Muito Alto"))
			riskResidualReturn = "Baixo";
		
		if(riskPotencial.equals("Baixo") && riskControlFinal.equals("Alto"))
			riskResidualReturn = "Baixo";
		
		if(riskPotencial.equals("Baixo") && riskControlFinal.equals("M�dio"))
			riskResidualReturn = "Baixo";
		
		if(riskPotencial.equals("Baixo") && riskControlFinal.equals("Baixo"))
			riskResidualReturn = "Baixo";
		
		return riskResidualReturn;
		
	}	
	
}
