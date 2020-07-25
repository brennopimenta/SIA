
            function limpaCamposCedente(){
            	document.getElementById("logradouro").value = '';
            	document.getElementById("bairro").value = '';
            	document.getElementById("cidade").value = '';
            	document.getElementById("uf").value = '';
                document.getElementById("emailCedente").value = '';
            	document.getElementById("logradouro").focus();
            }
            function limpaCpfProp(){
            	document.getElementById("cpfProp").value = '';
            	document.getElementById("cpfProp").focus();
            }

            function limpaCamposProprietario(){
            	document.getElementById("cpfProp").value = '';
                document.getElementById("propNome").value = '';
            	document.getElementById("logradouroProp").value = '';
            	document.getElementById("bairroProp").value = '';
            	document.getElementById("cidadeProp").value = '';
            	document.getElementById("ufProp").value = '';
            	document.getElementById("emailProp").value = '';
            	document.getElementById("cpfProp").focus();
            }
           
            function limpaCamposSigma(){
            	
            	document.getElementById("nome").value = '';
            	document.getElementById("arma").value = '';
            	document.getElementById("tabviewCadastroSigma:postoGraduacao").value = '';
            	document.getElementById("tabviewCadastroSigma:dataNascimento").value = '';
            	document.getElementById("tabviewCadastroSigma:rg").value = '';
            	document.getElementById("tabviewCadastroSigma:pai").value = '';
            	document.getElementById("tabviewCadastroSigma:mae").value = '';
            	document.getElementById("tabviewCadastroSigma:orgaoExpedidor").value = '';
            	document.getElementById("tabviewCadastroSigma:dataExpedicao").value = '';
            	document.getElementById("tabviewCadastroSigma:ufExpedidor").value = '';

            }

            function limpaBusca() {
                document.getElementById("globalFilter").value = '';
            }

            function ocultarPb(){
                document.getElementById("pb").style.display = 'none';
            }
            
            function exibirPb(){
              document.getElementById("pb").style.display = 'block';
              }



            function habilitarBtnProcessar(){
                document.getElementById("processar").disabled = false;
                document.getElementById("processar").title = "Clique para iniciar";

            }

            function desabilitarBtnProcessar(){
                document.getElementById("processar").disabled = true;
                document.getElementById("processar").title = "Para habilitar anexe um arquivo";
            }

			function executaFormAutenticado(){
            	document.getElementById("botaoPost").click();
            }

            function executaBotaoCancelar(){
                document.getElementById("botaoCancel").click();
            }

            function executaBotaoDownload(){
                document.getElementById("cargaTable:0:download").click();
            }

           //funções gerais
            function renderizar(componente){
                document.getElementById(componente).style.visibility = "visible";

            }
            
            function esconder(componente) {
                document.getElementById(componente).style.visibility = "hidden";
            }


            //***

            function hidde(){
                document.getElementById('check1').style.visibility = "hidden";
                document.getElementById('check2').style.visibility = "hidden";
                document.getElementById('check3').style.visibility = "hidden";
                document.getElementById('check4').style.visibility = "hidden";
                document.getElementById('check5').style.visibility = "hidden";
                document.getElementById('check6').style.visibility = "hidden";
                document.getElementById('check7').style.visibility = "hidden";
                document.getElementById('check8').style.visibility = "hidden";
                document.getElementById('check9').style.visibility = "hidden";
            }

