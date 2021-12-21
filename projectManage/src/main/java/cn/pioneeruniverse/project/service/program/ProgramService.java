package cn.pioneeruniverse.project.service.program;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import cn.pioneeruniverse.project.entity.TblProgramInfo;

public interface ProgramService {

	List<TblProgramInfo> getAllPrograms(TblProgramInfo programInfo, Long uid, List<String> roleCodes, Integer page,
			Integer rows);

	TblProgramInfo getProgramById(Long id);

	void updateProgram(TblProgramInfo tblProgramInfo, HttpServletRequest request);

}
