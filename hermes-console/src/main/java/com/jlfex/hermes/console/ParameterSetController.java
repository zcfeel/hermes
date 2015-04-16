package com.jlfex.hermes.console;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.jlfex.hermes.common.Logger;
import com.jlfex.hermes.common.dict.Dicts;
import com.jlfex.hermes.model.Dictionary;
import com.jlfex.hermes.model.DictionaryType;
import com.jlfex.hermes.service.ParameterSetService;
import com.jlfex.hermes.service.pojo.ParameterSetInfo;

/**
 * 
 * @author: lishunfeng
 * @time: 2015年1月6日 下午1:22:17
 */
@Controller
@RequestMapping("/parameter")
public class ParameterSetController {
	@Autowired
	private ParameterSetService parameterSetService;

	/**
	 * 参数设置首页
	 * 
	 */
	@RequestMapping("/index")
	public String index(Model model) {
		model.addAttribute("names", Dicts.elements(DictionaryType.Name.class).entrySet());
		return "/parameterset/parameterIndex";
	}

	/**
	 * 参数设置查询结果页面
	 * 
	 */
	@RequestMapping("/parameterdata")
	public String loandata(String parameterType, String parameterValue, String page, String size, Model model) {
		model.addAttribute("parameterSet", parameterSetService.findByParameterTypeAndParameterValue(parameterType, parameterValue, page, size));
		return "/parameterset/parameterdata";
	}

	/**
	 * 新增分类页面
	 * 
	 * @author lishunfeng
	 */
	@RequestMapping("/addParameter")
	public String addDictionary(@RequestParam(value = "id", required = true) String id,Model model) {
		DictionaryType dicType = parameterSetService.findOneById(id);
		model.addAttribute("dicType", dicType);
		return "/parameterset/addParameter";
	}
	/**
	 * 新增参数类型页面
	 * 
	 * @author lishunfeng
	 */
	@RequestMapping("/addParameterType")
	public String addParameterType(Model model) {
		return "/parameterset/addParameterType";
	}

	/**
	 * 处理新增参数配置逻辑
	 * 
	 * @author lishunfeng
	 */
	@RequestMapping("/handerAddDictionary")
	public String handerAddDictionary(ParameterSetInfo psVo, RedirectAttributes attr, Model model) {
		try {
			List<Dictionary> dicList = parameterSetService.findByNameAndType(psVo.getParameterValue(),psVo.getId());
			for(Dictionary dic:dicList){
				if(psVo.getCode().equals(dic.getCode())){
					attr.addFlashAttribute("msg", "类型编码已存在");
					return "redirect:/parameter/addParameter";
				}
			}
			if (dicList.size() > 0) {
				attr.addFlashAttribute("msg", "字典项已经存在");
				return "redirect:/parameter/addParameter";
			} else if (psVo.getParameterValue().equals("")) {
				attr.addFlashAttribute("msg", "字典项不能为空");
				return "redirect:/parameter/addParameter";
			} else {
				parameterSetService.addParameterSet(psVo);
				attr.addFlashAttribute("msg", "新增字典项成功");
				return "redirect:/parameter/index";
			}
		} catch (Exception e) {
			attr.addFlashAttribute("msg", "新增字典项失败");
			Logger.error("新增字典项失败：", e);
			return "redirect:/parameter/addParameter";
		}
	}
	/**
	 * 处理添加参数类业务
	 * 
	 * @author lishunfeng
	 */
	@RequestMapping("/handerAddParameterType")
	public String handerAddParameterType(String parameterType,String status,String typeCode,RedirectAttributes attr, Model model) {
		try {
			List<DictionaryType> dicList = parameterSetService.findByName(parameterType);
			for(DictionaryType dicType:dicList){
				if(typeCode.equals(dicType.getCode())){
					attr.addFlashAttribute("msg", "类型编码已存在");
					return "redirect:/parameter/addParameterType";
				}
			}
			if (dicList.size() > 0) {
				attr.addFlashAttribute("msg", "类型名称已经存在");
				return "redirect:/parameter/addParameterType";
			} else if (StringUtils.isEmpty(parameterType)) {
				attr.addFlashAttribute("msg", "类型名称不能为空");
				return "redirect:/parameter/addParameterType";
			} else {
				parameterSetService.addDictionaryType(parameterType, status,typeCode);
				attr.addFlashAttribute("msg", "添加类型成功");
				return "redirect:/parameter/index";
			}
		} catch (Exception e) {
			attr.addFlashAttribute("msg", "添加类型失败");
			Logger.error("添加类型失败：", e);
			return "redirect:/parameter/addParameterType";
		}
	}

	/**
	 * 编辑参数配置页面
	 * 
	 * @author lishunfeng
	 */
	@RequestMapping("/editParameter")
	public String editParameter(@RequestParam(value = "id", required = true) String id, Model model) {
		model.addAttribute("parameter", parameterSetService.findOne(id));
		return "/parameterset/editParameter";
	}

	/**
	 * 编辑参数类型页面
	 * 
	 * @author lishunfeng
	 */
	@RequestMapping("/editParameterType")
	public String editParameterType(@RequestParam(value = "id", required = true) String id, Model model) {
		model.addAttribute("parameter", parameterSetService.findOneById(id));
		return "/parameterset/editParameterType";
	}

	/**
	 * 处理逻辑参数配置逻辑
	 * 
	 * @author lishunfeng
	 */
	@RequestMapping("/handerEditParameter")
	public String handerEditParameter(ParameterSetInfo psVo, RedirectAttributes attr, Model model) {
		try {
			DictionaryType dictionaryType = parameterSetService.findOneByName(psVo.getParameterType());
			List<Dictionary> dicList = parameterSetService.findByNameAndType(psVo.getParameterValue(), dictionaryType.getId());
			for(Dictionary dic:dicList){
				if(psVo.getCode().equals(dic.getCode())){
					attr.addFlashAttribute("msg", "字典项编码已存在");
					return "redirect:/parameter/editParameter";
				}else if(psVo.getParameterValue().equals(dic.getName())){
					attr.addFlashAttribute("msg", "字典项已存在");
					return "redirect:/parameter/editParameter";					
				}
			}
            if (psVo.getParameterValue().equals("")) {
				attr.addFlashAttribute("msg", "字典项不能为空");
				return "redirect:/parameter/editParameter";
			}
			parameterSetService.updateDictionary(psVo);
			attr.addFlashAttribute("msg", "字典项修改成功");
			return "redirect:/parameter/index";
		} catch (Exception e) {
			attr.addFlashAttribute("msg", "字典项修改失败");
			Logger.error("字典项修改失败：", e);
			return "redirect:/parameter/editParameter";
		}
	}

	/**
	 * 修改参数类型
	 * 
	 * @author lishunfeng
	 */
	@RequestMapping("/handerEditParameterType")
	public String handerEditParameterType(String parameterType,String id, String typeCode,RedirectAttributes attr, Model model) {
		try {
			List<DictionaryType> dicList = parameterSetService.findByName(parameterType);
			for(DictionaryType dicType:dicList){
				if(typeCode.equals(dicType.getCode())){
					attr.addFlashAttribute("msg", "类型编码已存在");
					return "redirect:/parameter/editParameterType";
				}
			}
            if (StringUtils.isEmpty(parameterType)) {
				attr.addFlashAttribute("msg", "类型名称不能为空");
				return "redirect:/parameter/editParameterType";
			}
			parameterSetService.updateDicType(parameterType, id,typeCode);
			attr.addFlashAttribute("msg", "类型修改成功");
			return "redirect:/parameter/index";
		} catch (Exception e) {
			attr.addFlashAttribute("msg", "类型修改失败");
			Logger.error("类型修改失败：", e);
			return "redirect:/parameter/editParameterType";
		}
	}

	@RequestMapping(value = "/switch", method = RequestMethod.POST)
	public String switchDictionary(@RequestParam(value = "id", required = true) String id, RedirectAttributes attr, Model model) {
		Dictionary dictionary = parameterSetService.findOne(id);
		try {
			parameterSetService.switchDictionary(id);
			if (("00").equals(dictionary.getStatus())) {
				attr.addFlashAttribute("msg", "禁用成功");
			} else {
				attr.addFlashAttribute("msg", "启用成功");
			}
		} catch (Exception e) {
			if (("00").equals(dictionary.getStatus())) {
				attr.addFlashAttribute("msg", "禁用失败");
			} else {
				attr.addFlashAttribute("msg", "启用失败");
			}
		}
		return "redirect:/parameter/index";
	}
}