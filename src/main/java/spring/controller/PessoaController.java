package spring.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
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
import spring.repository.TelefoneRepository;

@Controller
public class PessoaController {
	
	@Autowired
	private PessoaRepository pessoaRepository;
	
	@Autowired
	private TelefoneRepository telefoneRepository;
	
	@RequestMapping(method = RequestMethod.GET, value = "/cadastropessoa")
	public ModelAndView inicio() {
		
		//prepara o retorno do modo envio para a mesma tela
		ModelAndView modelAndView = new ModelAndView("cadastro/cadastropessoa");
				
		//retorna um objeto vazio, para evitar erro
		modelAndView.addObject("pessoaobj", new Pessoa());
		
		//carrega lista de pessoas ao abrir a pagina
		Iterable<Pessoa> pessoasIt = pessoaRepository.findAll();
		modelAndView.addObject("pessoas", pessoasIt);
		
		return modelAndView;
	}
	
	@RequestMapping(method = RequestMethod.POST, value = "**/salvarpessoa")//** ignora tudo que vem antes do /salvarpessoa
	public ModelAndView salvar(Pessoa pessoa) {
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
	public ModelAndView pesquisar(@RequestParam("nomepesquisa") String nomepesquisa) {
		//voltar pra mesma tela de cadastro
		ModelAndView modelAndView = new ModelAndView("cadastro/cadastropessoa");
		
		modelAndView.addObject("pessoas", pessoaRepository.findPessoaByName(nomepesquisa));
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
		
		return modelAndView;
	}
	
	//intercepta a url, ignora o que vem antes e pega o parametro
	@PostMapping("**/addfonepessoa/{pessoaid}")
	public ModelAndView addFonePessoa(Telefone telefone, @PathVariable("pessoaid") Long pessoaid) {//pega os dados do formulario, pega o pathvariable para pegar o id da pessoa
		
		Pessoa pessoa = pessoaRepository.findById(pessoaid).get();
		telefone.setPessoa(pessoa);
		telefoneRepository.save(telefone);
		
		//retorna para a tela de telefones após a execução.
		ModelAndView modelAndView = new ModelAndView("cadastro/telefones");
		//precisa do objeto pai sendo mostrado, para redirecionar e exibir na tela
		modelAndView.addObject("pessoaobj", pessoa);
		return modelAndView;
	}
	

}
