package spring.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import spring.entidade.Pessoa;
import spring.entidade.Telefone;
import spring.repository.PessoaRepository;
import spring.repository.ProfissaoRepository;
import spring.repository.TelefoneRepository;

@Controller
public class PessoaController {
	
	@Autowired
	private PessoaRepository pessoaRepository;
	
	@Autowired
	private TelefoneRepository telefoneRepository;
	
	@Autowired
	private ReportUtil reportUtil;
	
	@Autowired
	private ProfissaoRepository profissaoRepository;
	
	@RequestMapping(method = RequestMethod.GET, value = "/cadastropessoa")
	public ModelAndView inicio() {
		
		//prepara o retorno do modo envio para a mesma tela
		ModelAndView modelAndView = new ModelAndView("cadastro/cadastropessoa");
				
		//retorna um objeto vazio, para evitar erro
		modelAndView.addObject("pessoaobj", new Pessoa());
		
		//carrega lista de pessoas ao abrir a pagina
		Iterable<Pessoa> pessoasIt = pessoaRepository.findAll();
		modelAndView.addObject("pessoas", pessoasIt);
		modelAndView.addObject("profissoes", profissaoRepository.findAll());
		
		return modelAndView;
	}
	
	@RequestMapping(method = RequestMethod.POST, value = "**/salvarpessoa")//** ignora tudo que vem antes do /salvarpessoa
	public ModelAndView salvar(@Valid Pessoa pessoa, BindingResult bindingResult) {
		
		pessoa.setTelefones(telefoneRepository.getTelefones(pessoa.getId()));
		
		if(bindingResult.hasErrors()) {
			ModelAndView modelAndView = new ModelAndView("cadastro/cadastropessoa");
			Iterable<Pessoa> pessoasIt = pessoaRepository.findAll();
			modelAndView.addObject("pessoas", pessoasIt);
			modelAndView.addObject("pessoaobj", pessoa);
			
			List<String> msg = new ArrayList<String>();
			for(ObjectError objectError : bindingResult.getAllErrors()) {
				msg.add(objectError.getDefaultMessage()); //vem das anotações das validações feitas nas entidades.
			}
			modelAndView.addObject("msg", msg);
			modelAndView.addObject("profissoes", profissaoRepository.findAll());
			return modelAndView;
		} 
			pessoaRepository.save(pessoa);
			
			ModelAndView andVier = new ModelAndView("cadastro/cadastropessoa");
			Iterable<Pessoa> pessoasIt = pessoaRepository.findAll();
			andVier.addObject("pessoas", pessoasIt);
			andVier.addObject("pessoaobj", new Pessoa());
			
			
			return andVier;
		
		
		
	}
	
	@RequestMapping(method = RequestMethod.GET, value="/listapessoas")
	public ModelAndView pessoas() {
		ModelAndView andVier = new ModelAndView("cadastro/cadastropessoa");
		Iterable<Pessoa> pessoasIt = pessoaRepository.findAll();
		andVier.addObject("pessoas", pessoasIt);
		andVier.addObject("pessoaobj", new Pessoa());
		return andVier;
	}
	
	
	@GetMapping("/editarpessoa/{idpessoa}") //mapeou a url criada no html
	public ModelAndView editar(@PathVariable("idpessoa") Long idpessoa) {
		//carrega/consulta o objeto do banco de dados
		Optional<Pessoa> pessoa = pessoaRepository.findById(idpessoa);
		
		//prepara o retorno do modo envio para a mesma tela
		ModelAndView modelAndView = new ModelAndView("cadastro/cadastropessoa");
		
		//prepara o objeto para edição
		modelAndView.addObject("pessoaobj", pessoa.get());
		modelAndView.addObject("profissoes", profissaoRepository.findAll());
		return modelAndView;
	}
	
	@GetMapping("/removerpessoa/{idpessoa}") //mapeou a url criada no html
	public ModelAndView excluir(@PathVariable("idpessoa") Long idpessoa) {//pathvariable recebe o valor do pessoa.id
		//carrega/consulta o objeto do banco de dados
		pessoaRepository.deleteById(idpessoa);
		
		//prepara o retorno do modo envio para a mesma tela
		ModelAndView modelAndView = new ModelAndView("cadastro/cadastropessoa");
		
		//prepara o objeto para edição
		modelAndView.addObject("pessoas", pessoaRepository.findAll());
		modelAndView.addObject("pessoaobj", new Pessoa());
		
		return modelAndView;
	}
	
	@PostMapping("**/pesquisarpessoa")
	public ModelAndView pesquisar(@RequestParam("nomepesquisa") String nomepesquisa,
			@RequestParam("pesqsexo") String pesqsexo) {
		
		List<Pessoa> pessoas = new ArrayList<Pessoa>();
		
		if(pesqsexo != null && !pesqsexo.isEmpty()) {
			pessoas = pessoaRepository.findPessoaByNameSexo(nomepesquisa, pesqsexo);
		}else {
			pessoas = pessoaRepository.findPessoaByName(nomepesquisa);
		}
		
		//voltar pra mesma tela de cadastro
		ModelAndView modelAndView = new ModelAndView("cadastro/cadastropessoa");
		
		modelAndView.addObject("pessoas", pessoas);
		modelAndView.addObject("pessoaobj", new Pessoa());
		
		return modelAndView;
		
	}
	
	@GetMapping("/telefones/{idpessoa}") //mapeou a url criada no html
	public ModelAndView telefones(@PathVariable("idpessoa") Long idpessoa) {
		//carrega/consulta o objeto do banco de dados
		Optional<Pessoa> pessoa = pessoaRepository.findById(idpessoa);
		
		//prepara o retorno do modo envio para a mesma tela
		ModelAndView modelAndView = new ModelAndView("cadastro/telefones");
		
		//prepara o objeto para edição
		modelAndView.addObject("pessoaobj", pessoa.get());
		modelAndView.addObject("telefones", telefoneRepository.getTelefones(idpessoa));
		
		
		return modelAndView;
	}
	
	//intercepta a url, ignora o que vem antes e pega o parametro
	@PostMapping("**/addfonepessoa/{pessoaid}")
	public ModelAndView addFonePessoa(Telefone telefone, @PathVariable("pessoaid") Long pessoaid) {//pega os dados do formulario, pega o pathvariable para pegar o id da pessoa
		
		Pessoa pessoa = pessoaRepository.findById(pessoaid).get();
		
		if(telefone != null && telefone.getNumero().isEmpty() || telefone.getTipo().isEmpty())  {
			//retorna para a tela de telefones após a execução.
			ModelAndView modelAndView = new ModelAndView("cadastro/telefones");
			//precisa do objeto pai sendo mostrado, para redirecionar e exibir na tela
			modelAndView.addObject("pessoaobj", pessoa);
			//pega o objeto telefones e chama a query criado para tarzer do banco
			modelAndView.addObject("telefones", telefoneRepository.getTelefones(pessoaid));
			
			List<String> msg = new ArrayList<String>();
			if(telefone.getNumero().isEmpty()) {
				msg.add("Numero deve ser informado.");
			}
			if(telefone.getTipo().isEmpty()) {
				msg.add("Tipo deve ser informado.");
			}
			
			modelAndView.addObject("msg", msg);
			
			return modelAndView;
		}
		//retorna para a tela de telefones após a execução.
		ModelAndView modelAndView = new ModelAndView("cadastro/telefones");
		
		telefone.setPessoa(pessoa);
		telefoneRepository.save(telefone);
		
		//precisa do objeto pai sendo mostrado, para redirecionar e exibir na tela
		modelAndView.addObject("pessoaobj", pessoa);
		//pega o objeto telefones e chama a query criado para tarzer do banco
		modelAndView.addObject("telefones", telefoneRepository.getTelefones(pessoaid));	
		
		return modelAndView;
	}
	
	@GetMapping("/removertelefone/{idtelefone}") //mapeou a url criada no html
	public ModelAndView removertelefone(@PathVariable("idtelefone") Long idtelefone) {//pathvariable recebe o valor do pessoa.id
		
		Pessoa pessoa = telefoneRepository.findById(idtelefone).get().getPessoa();
		
		
		telefoneRepository.deleteById(idtelefone);
		
		
		ModelAndView modelAndView = new ModelAndView("cadastro/telefones");
		
		
		modelAndView.addObject("pessoas", pessoaRepository.findAll());
		modelAndView.addObject("pessoaobj", pessoa);
		modelAndView.addObject("telefones", telefoneRepository.getTelefones(pessoa.getId()));
		
		return modelAndView;
	}
	
	@GetMapping("**/pesquisarpessoa")
	public void imprimePdf(@RequestParam("nomepesquisa") String nomepesquisa,
			@RequestParam("pesqsexo") String pesqsexo,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		List<Pessoa> pessoas = new ArrayList<Pessoa>();
		/*Busca por nome e sexo se for digitado*/
		if(pesqsexo != null && !pesqsexo.isEmpty() && nomepesquisa != null && !nomepesquisa.isEmpty()) {
			pessoas = pessoaRepository.findPessoaByNameSexo(nomepesquisa, pesqsexo);
			
		/*Pesquisa só por nome se for digitado*/	
		}else if(nomepesquisa != null && !nomepesquisa.isEmpty()) {
			pessoas = pessoaRepository.findPessoaByName(nomepesquisa);
		/*se nao for digitado nada pega toda a lista*/
		}else if(pesqsexo != null && !pesqsexo.isEmpty()) {
			pessoas = pessoaRepository.findPessoaBySexo(pesqsexo);
		/*se nao for digitado nada pega toda a lista*/	
		}else {
			Iterable<Pessoa> iterator = pessoaRepository.findAll();
			for(Pessoa pessoa : iterator) {
				pessoas.add(pessoa);
			}
		}
		
		/*chamar o servico que faz a geração do relatorio*/
		byte[] pdf = reportUtil.gerarRelatorio(pessoas, "pessoa", request.getServletContext());
		
		/*Tamanho da resposta*/
		response.setContentLength(pdf.length);
		
		/*Definir na resposta o tipo de arquivo*/
		response.setContentType("application/octet-stream");
		
		/*Definir o cabeçalho da resposta*/
		String headerKey = "Content-Disposition";
		String headerValue = String.format("attachment; filename=\"%s\"", "Relatorio.pdf");
		
		response.setHeader(headerKey, headerValue);
		
		/*Finaliza a resposta pro navegador*/
		response.getOutputStream().write(pdf);
		
	}
	

}
