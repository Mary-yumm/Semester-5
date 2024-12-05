#include <cassert>
#include <cstdio>
#include <cstdlib>
#include <cstring>
#include<iostream>
#include "instructions.h"

// ========== InstructionBase ==========
void InstructionBase::execute(ProcessorState& state) const {
  // virtual call that implements the actual functionality of the instruction
  _execute(state);

  // move the pc forward
  state.pc += INSTRUCTION_SIZE;

  // trim the accumulator and the PC to fit in number of bits of the architecture
  state.acc &= ARCH_BITMASK;
  state.pc &= ARCH_BITMASK;
}

addr_t InstructionBase::get_address() const {
  return _address;
}

void InstructionBase::_set_address(addr_t address) {
  _address = address & ARCH_BITMASK;
}

char* InstructionBase::to_string() const {
    char* buffer = (char*)malloc(256); // Increased size for safety
    if (!buffer) {
        fprintf(stderr, "Memory allocation failed in to_string().\n");
        std::terminate();
    }
    if (strncmp(name(), "ADD", 3) == 0)
        snprintf(buffer, 256, "%s: ACC <- ACC + [%d]", name(), get_address());
    else if (strncmp(name(), "AND", 3) == 0)
        snprintf(buffer, 256, "%s: ACC <- ACC & [%d]", name(), get_address());
    else if (strncmp(name(), "ORR", 3) == 0)
        snprintf(buffer, 256, "%s: ACC <- ACC | [%d]", name(), get_address());
    else if (strncmp(name(), "XOR", 3) == 0)
        snprintf(buffer, 256, "%s: ACC <- ACC ^ [%d]", name(), get_address());
    else if (strncmp(name(), "LDR", 3) == 0)
        snprintf(buffer, 256, "%s: ACC <- [%d]", name(), get_address());
    else if (strncmp(name(), "STR", 3) == 0)
        snprintf(buffer, 256, "%s: ACC -> [%d]", name(), get_address());
    else if (strncmp(name(), "JMP", 3) == 0)
        snprintf(buffer, 256, "%s: PC  <- %d", name(), get_address());
    else if (strncmp(name(), "JNE", 3) == 0)
        snprintf(buffer, 256, "%s: PC  <- %d if ACC != 0", name(), get_address());
    else
        assert(0);
    return buffer;
}


#include <memory> // For std::unique_ptr

std::unique_ptr<InstructionBase> InstructionBase::generateInstruction(InstructionData data) {
    if (data.opcode == ADD)
        return std::make_unique<Iadd>(data.address);
    if (data.opcode == AND)
        return std::make_unique<Iand>(data.address);
    if (data.opcode == ORR)
        return std::make_unique<Iorr>(data.address);
    if (data.opcode == XOR)
        return std::make_unique<Ixor>(data.address);
    if (data.opcode == LDR)
        return std::make_unique<Ildr>(data.address);
    if (data.opcode == STR)
        return std::make_unique<Istr>(data.address);
    if (data.opcode == JMP)
        return std::make_unique<Ijmp>(data.address);
    if (data.opcode == JNE)
        return std::make_unique<Ijne>(data.address);

    return nullptr;
}


// ========== ADD Instruction ==========
Iadd::Iadd(addr_t address) {
  _set_address(address);
}

void Iadd::_execute(ProcessorState& state) const {
  state.acc += state.memory[get_address()];
}

const char* Iadd::name() const {
  return "ADD";
}

// ========== AND Instruction ==========
Iand::Iand(addr_t address) {
  _set_address(address);
}

void Iand::_execute(ProcessorState& state) const {
  state.acc &= state.memory[get_address()];
}

const char* Iand::name() const {
  return "AND";
}

// ========== ORR Instruction ==========
Iorr::Iorr(addr_t address) {
  _set_address(address);
}

void Iorr::_execute(ProcessorState& state) const {
  state.acc |= state.memory[get_address()];
}

const char* Iorr::name() const {
  return "ORR";
}

// ========== XOR Instruction ==========
Ixor::Ixor(addr_t address) {
  _set_address(address);
}


void Ixor::_execute(ProcessorState& state) const {
  state.acc ^= state.memory[get_address()];
}

const char* Ixor::name() const {
  return "XOR";
}

// ========== LDR Instruction ==========
Ildr::Ildr(addr_t address) {
  _set_address(address);
}

void Ildr::_execute(ProcessorState& state) const {
  state.acc = state.memory[get_address()];
}

const char* Ildr::name() const {
  return "LDR";
}

// ========== STR Instruction ==========
Istr::Istr(addr_t address) {
  _set_address(address);
}

void Istr::_execute(ProcessorState& state) const {
  state.memory[get_address()] = state.acc;
}

const char* Istr::name() const {
  return "STR";
}

// ========== JMP Instruction ==========
Ijmp::Ijmp(addr_t address) {
  _set_address(address);
}

void Ijmp::_execute(ProcessorState& state) const {
  // Why minus two? Because execute() will increment PC by two,
  // so to make the PC take (eventually) the value `address`
  // I need to subtract two here. Same applies for JNE below
  // This kind of unintuitive behaviour is a clear sign of bad
  // class hierarchy design 
  state.pc = get_address() - 2;
}

const char* Ijmp::name() const {
  return "JMP";
}

// ========== JNE Instruction ==========
Ijne::Ijne(addr_t address) {
  _set_address(address);
}

void Ijne::_execute(ProcessorState& state) const {
  // Same hack as above
  if (state.acc != 0)
    state.pc = get_address() - 2;
}

const char* Ijne::name() const {
  return "JNE";
}

